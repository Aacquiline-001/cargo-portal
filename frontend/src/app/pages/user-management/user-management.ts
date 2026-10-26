import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Button } from 'primeng/button';
import { Tabs, TabList, Tab, TabPanels, TabPanel } from 'primeng/tabs';
import { Toast } from 'primeng/toast';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Auth } from '../../services/auth';
import { ThemeService } from '../../services/theme-service';
import { AdminService } from '../../services/admin-service';
import { AgentService } from '../../services/agent-service';
import { AdminTab } from './admin-tab/admin-tab';
import { AgentTab } from './agent-tab/agent-tab';

@Component({
  selector: 'app-user-management',
  imports: [Button, Tabs, TabList, Tab, TabPanels, TabPanel, Toast, ConfirmDialog, AdminTab, AgentTab],
  providers: [MessageService, ConfirmationService],
  templateUrl: './user-management.html',
  styleUrl: './user-management.css',
})
export class UserManagement implements OnInit {

  username: string | null = '';
  initials = '';

  // numbers for the 3 summary cards
  adminCount = signal(0);
  agentCount = signal(0);
  inactiveCount = signal(0);

  // admins/agents that are not active (added together for the 3rd card)
  private inactiveAdmins = 0;
  private inactiveAgents = 0;

  constructor(private auth: Auth,
              private router: Router,
              public themeService: ThemeService,
              private adminService: AdminService,
              private agentService: AgentService) {
    this.username = this.auth.getUsername();
    if (this.username) {
      this.initials = this.username.substring(0, 2).toUpperCase();
    }
  }

  ngOnInit(): void {
    this.loadAdminStats();
    this.loadAgentStats();
  }

  // called at start, and again whenever the Admin tab adds/edits/deletes
  loadAdminStats(): void {
    this.adminService.search('', '', '').subscribe({
      next: (admins) => {
        this.adminCount.set(admins.length);
        this.inactiveAdmins = 0;
        for (const admin of admins) {
          if (!admin.active) {
            this.inactiveAdmins++;
          }
        }
        this.inactiveCount.set(this.inactiveAdmins + this.inactiveAgents);
      }
    });
  }

  // called at start, and again whenever the Agent tab adds/edits/deletes
  loadAgentStats(): void {
    this.agentService.search('', '', '').subscribe({
      next: (agents) => {
        this.agentCount.set(agents.length);
        this.inactiveAgents = 0;
        for (const agent of agents) {
          if (!agent.active) {
            this.inactiveAgents++;
          }
        }
        this.inactiveCount.set(this.inactiveAdmins + this.inactiveAgents);
      }
    });
  }

  onLogout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}