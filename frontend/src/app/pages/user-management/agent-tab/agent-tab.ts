import { Component, OnInit, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { Button } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Dialog } from 'primeng/dialog';
import { Select } from 'primeng/select';
import { Checkbox } from 'primeng/checkbox';
import { Tag } from 'primeng/tag';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AgentService } from '../../../services/agent-service';
import { Agent, AgentRequest } from '../../../models/agent';

@Component({
  selector: 'app-agent-tab',
  imports: [FormsModule, TableModule, Button, InputText, Dialog, Select, Checkbox, Tag],
  templateUrl: './agent-tab.html',
  styleUrl: './agent-tab.css',
})
export class AgentTab implements OnInit {

  // ---------- data shown on screen (signals) ----------
  agents = signal<Agent[]>([]);
  loading = signal(false);
  dialogVisible = signal(false);
  saving = signal(false);

  // ---------- search boxes ----------
  searchLoginId = '';
  searchPrivilege = '';
  searchAgentCode = '';

  // ---------- the form inside the popup ----------
  form: AgentRequest = this.emptyForm();
  editingPk: number | null = null;    // null = Add, a number = Edit

  // ---------- dropdown options ----------
  languages = ['English', 'Japanese', 'Korean'];
  userTypes = ['Agent 1', 'Agent 2', 'Agent 3'];
  privileges = ['Supervisor1', 'Supervisor2', 'Supervisor3'];

  constructor(private agentService: AgentService,
              private messageService: MessageService,
              private confirmationService: ConfirmationService) {}

  ngOnInit(): void {
    this.loadAgents();
  }

  // =============== LIST / SEARCH ===============
  loadAgents(): void {
    this.loading.set(true);
    this.agentService.search(this.searchLoginId, this.searchPrivilege, this.searchAgentCode).subscribe({
      next: (data) => {
        this.agents.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.showError(err);
      }
    });
  }

  clearSearch(): void {
    this.searchLoginId = '';
    this.searchPrivilege = '';
    this.searchAgentCode = '';
    this.loadAgents();
  }

  // =============== OPEN THE POPUP ===============
  openAdd(): void {
    this.editingPk = null;
    this.form = this.emptyForm();
    this.dialogVisible.set(true);
  }

  openEdit(agent: Agent): void {
    this.editingPk = agent.agentPk;
    this.form = {
      loginId: agent.loginId,
      userName: agent.userName,
      language: agent.language,
      emailId: agent.emailId,
      userType: agent.userType,
      privilege: agent.privilege,
      agentCode: agent.agentCode,
      companyName: agent.companyName,
      address: agent.address,
      telephone: agent.telephone,
      active: agent.active
    };
    this.dialogVisible.set(true);
  }

  // =============== SAVE (Add or Update) ===============
  save(agentForm: NgForm): void {
    if (agentForm.invalid) {
      agentForm.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);

    if (this.editingPk === null) {
      this.agentService.create(this.form).subscribe({
        next: () => this.afterSave('Agent user added successfully'),
        error: (err) => {
          this.saving.set(false);
          this.showError(err);
        }
      });
    } else {
      this.agentService.update(this.editingPk, this.form).subscribe({
        next: () => this.afterSave('Agent user updated successfully'),
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
    this.loadAgents();
  }

  // =============== DELETE ===============
  confirmDelete(agent: Agent): void {
    this.confirmationService.confirm({
      header: 'Delete Agent User',
      message: 'Are you sure you want to delete ' + agent.loginId + '?',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Yes',
      rejectLabel: 'No',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary p-button-text',
      accept: () => {
        this.agentService.delete(agent.agentPk).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Deleted', detail: agent.loginId + ' deleted' });
            this.loadAgents();
          },
          error: (err) => this.showError(err)
        });
      }
    });
  }

  // =============== HELPERS ===============
  private emptyForm(): AgentRequest {
    return {
      loginId: '',
      userName: '',
      language: '',
      emailId: '',
      userType: '',
      privilege: '',
      agentCode: '',
      companyName: '',
      address: '',
      telephone: '',
      active: true
    };
  }

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