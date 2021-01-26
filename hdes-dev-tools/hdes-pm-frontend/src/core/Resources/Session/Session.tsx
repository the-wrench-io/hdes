
declare namespace Session {
  interface Tab {
    id: string;
    label: string;
    panel: React.ReactNode;
    data?: any;
    edit?: boolean;
  }
  
  interface History {
    previous?: History;
    open: number;
  }
  
  interface Instance {  
    tabs: Tab[];
    history: History;
    dialogId?: string;
    search?: string;
    
    withSearch(keyword: string): Instance;
    withDialog(dialogId?: string): Instance;
    withTabData(tabId: string, updateCommand: (oldData: any) => any): Instance;
    withTab(newTabOrTabIndex: Tab | number): Instance;
    findTab(newTabId: string): number | undefined;
    getTabData(tabId: string): any;
    deleteTab(tabId: string): Session.Instance;
  }
}

export type { Session };