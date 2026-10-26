import { Component, EventEmitter, OnInit, Output, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { Button } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Dialog } from 'primeng/dialog';
import { Select } from 'primeng/select';
import { Checkbox } from 'primeng/checkbox';
import { Tag } from 'primeng/tag';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AdminService } from '../../../services/admin-service';
import { Admin, AdminRequest } from '../../../models/admin';

@Component({
  selector: 'app-admin-tab',
  imports: [FormsModule, TableModule, Button, InputText, Dialog, Select, Checkbox, Tag],
  templateUrl: './admin-tab.html',
  styleUrl: './admin-tab.css',
})
export class AdminTab implements OnInit {
  
  // tells the parent page "something changed" so it can refresh the summary cards
  @Output() changed = new EventEmitter<void>();

  // ---------- data shown on screen (signals) ----------
  admins = signal<Admin[]>([]);       // rows of the table
  loading = signal(false);            // table spinner
  dialogVisible = signal(false);      // is the Add/Edit popup open?
  saving = signal(false);             // spinner on the Save button

  // ---------- search boxes ----------
  searchAdminId = '';
  searchRegion = '';
  searchEmpNo = '';

  // ---------- the form inside the popup ----------
  form: AdminRequest = this.emptyForm();
  editingPk: number | null = null;    // null = Add, a number = Edit that admin

  // ---------- dropdown options ----------
  regions = ['India', 'America', 'Korea', 'Japan'];
  branches = ['ibs-12', 'ibs-13', 'ibs-14', 'ibs-15'];
  languages = ['English', 'Japanese', 'Korean'];

  constructor(private adminService: AdminService,
              private messageService: MessageService,
              private confirmationService: ConfirmationService) {}

  // runs once when the tab appears
  ngOnInit(): void {
    this.loadAdmins();
  }

  // =============== LIST / SEARCH ===============
  loadAdmins(): void {
    this.loading.set(true);
    this.adminService.search(this.searchAdminId, this.searchRegion, this.searchEmpNo).subscribe({
      next: (data) => {
        this.admins.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.showError(err);
      }
    });
  }

  clearSearch(): void {
    this.searchAdminId = '';
    this.searchRegion = '';
    this.searchEmpNo = '';
    this.loadAdmins();
  }

  // =============== OPEN THE POPUP ===============
  openAdd(): void {
    this.editingPk = null;
    this.form = this.emptyForm();
    this.dialogVisible.set(true);
  }

  openEdit(admin: Admin): void {
    this.editingPk = admin.adminPk;
    // copy the row's values into the form
    this.form = {
      adminId: admin.adminId,
      adminName: admin.adminName,
      region: admin.region,
      branch: admin.branch,
      language: admin.language,
      employeeNumber: admin.employeeNumber,
      contactNumber: admin.contactNumber,
      emailId: admin.emailId,
      active: admin.active
    };
    this.dialogVisible.set(true);
  }

  // =============== SAVE (Add or Update) ===============
  save(adminForm: NgForm): void {
    // stop if a required field is empty or the email is invalid
    if (adminForm.invalid) {
      adminForm.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);

    if (this.editingPk === null) {
      // ----- ADD -----
      this.adminService.create(this.form).subscribe({
        next: () => this.afterSave('Admin added successfully'),
        error: (err) => {
          this.saving.set(false);
          this.showError(err);
        }
      });
    } else {
      // ----- UPDATE -----
      this.adminService.update(this.editingPk, this.form).subscribe({
        next: () => this.afterSave('Admin updated successfully'),
        error: (err) => {
          this.saving.set(false);
          this.showError(err);
        }
      });
    }
  }

  private afterSave(text: string): void {
    this.saving.set(false);
    this.dialogVisible.set(false);
    this.messageService.add({ severity: 'success', summary: 'Success', detail: text });
    this.loadAdmins();   // refresh the table
    this.changed.emit(); 
  }

  // =============== DELETE ===============
  confirmDelete(admin: Admin): void {
    this.confirmationService.confirm({
      header: 'Delete Admin',
      message: 'Are you sure you want to delete ' + admin.adminId + '?',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Yes',
      rejectLabel: 'No',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary p-button-text',
      accept: () => {
        this.adminService.delete(admin.adminPk).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Deleted', detail: admin.adminId + ' deleted' });
            this.loadAdmins();
            this.changed.emit();
          },
          error: (err) => this.showError(err)
        });
      }
    });
  }

  // =============== HELPERS ===============
  private emptyForm(): AdminRequest {
    return {
      adminId: '',
      adminName: '',
      region: '',
      branch: '',
      language: '',
      employeeNumber: '',
      contactNumber: '',
      emailId: '',
      active: true
    };
  }

  // Show the backend's error message (409 duplicate, 400 validation, 404 ...) in a toast
  private showError(err: any): void {
    let text = 'Something went wrong';
    if (err.status === 0) {
      text = 'Cannot reach the server';
    } else if (err.error && err.error.fieldErrors) {
      text = Object.values(err.error.fieldErrors).join(', ');
    } else if (err.error && err.error.message) {
      text = err.error.message;
    }
    this.messageService.add({ severity: 'error', summary: 'Error', detail: text });
  }
}