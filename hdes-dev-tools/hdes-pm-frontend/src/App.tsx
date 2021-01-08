import React from 'react';

import clsx from 'clsx';

import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';

import LibraryAddOutlinedIcon from '@material-ui/icons/LibraryAddOutlined';
import PersonAddOutlinedIcon from '@material-ui/icons/PersonAddOutlined';

import AppsOutlinedIcon from '@material-ui/icons/AppsOutlined';
import GroupAddOutlinedIcon from '@material-ui/icons/GroupAddOutlined';
import GroupOutlinedIcon from '@material-ui/icons/GroupOutlined';
import LibraryBooksOutlinedIcon from '@material-ui/icons/LibraryBooksOutlined';
import PersonOutlineOutlinedIcon from '@material-ui/icons/PersonOutlineOutlined';

import { Resources, Backend, Session } from './core/Resources';

import Shell from './core/Shell';
import { AddUser, ConfigureUser, UsersView } from './core/Users';
import { AddProject, ProjectsView } from './core/Projects';
import { AddGroup, GroupsView } from './core/Groups';


const useStyles = makeStyles((theme) => ({
  paper: {
    padding: theme.spacing(2),
    display: 'flex',
    overflow: 'auto',
    flexDirection: 'column',
  },
  fixedHeight: {
    height: 340,
  },
}));

const makeDialogs = () => {
  return {
    user:     { id: 'add-user', label: 'Add User', icon: <PersonAddOutlinedIcon />},
    project:  { id: 'add-project', label: 'Add Project', icon: <LibraryAddOutlinedIcon /> },
    group:    { id: 'add-group', label: 'Add Group', icon: <GroupAddOutlinedIcon />}
  }
}

function App() {
  const { session, setSession } = React.useContext(Resources.Context);
  
  const classes = useStyles();
  const dialogs = makeDialogs();
  const fixedHeightPaper = clsx(classes.paper, classes.fixedHeight);  
  

  const handleDialogOpen = (id: string) => {
    setSession((session) => {
      const index = session.findTab(id);
      return index ? session.withTab(index) : session.withDialog(id);
    });
  };
  const handleDialogClose = () => setSession((session) => session.withDialog());
  const changeTab = (index: number) => setSession((session) => session.withTab(index));
  const addTab = (newItem: Session.Tab) => setSession((session) => session.withTab(newItem));
  const setTabData = (id: string, data: any) => setSession((session) => session.withTabData(id, data));
  
  const confUserInTab = (user: Backend.UserBuilder, activeStep?: number) => {
    const id: string = user.id ? user.id : dialogs.user.id;
    const label: string = user.id ? user.name + '' : 'create user';
    const panel = <ConfigureUser activeStep={activeStep ? activeStep : 0} 
      setUser={(data) => setTabData(id, data)} 
      getUser={() => session.getTabData(id, user)} />;
    addTab({id, label, panel});
  };
  
  const listDashboard = () => addTab({id: 'dashboard', label: 'Dashboard', panel: <React.Fragment>{projects}{users}</React.Fragment>});
  const listGroups    = () => addTab({id: 'groups', label: 'Groups', panel: <GroupsView />});
  const listProjects  = () => addTab({id: 'projects', label: 'Projects', panel: <ProjectsView />});
  const listUsers     = () => addTab({id: 'users', label: 'Users', panel: <UsersView onEdit={confUserInTab}/>});

  const projects = (<Grid key="1" item xs={12} md={8} lg={9}>
              <Paper className={fixedHeightPaper}>
                <ProjectsView top={4} seeMore={listProjects}/>
              </Paper>
            </Grid>)

  const users = (<Grid key="2" item xs={12} md={8} lg={9}>
              <Paper className={fixedHeightPaper}>
                <UsersView top={4} seeMore={listUsers} onEdit={confUserInTab}/>
              </Paper>
            </Grid>)
  
  const views = [
    { label: 'Dashboard', icon: <AppsOutlinedIcon />, onClick: listDashboard},
    { label: 'List Groups', icon: <GroupOutlinedIcon />, onClick: listGroups},
    { label: 'List Users', icon: <PersonOutlineOutlinedIcon />, onClick: listUsers},
    { label: 'List Projects', icon: <LibraryBooksOutlinedIcon />, onClick: listProjects}
  ];

  return (<React.Fragment>
    <AddUser open={session.dialogId === dialogs.user.id} handleClose={handleDialogClose} handleConf={confUserInTab} />
    <AddProject open={session.dialogId === dialogs.project.id} handleClose={handleDialogClose} />
    <AddGroup open={session.dialogId === dialogs.group.id} handleClose={handleDialogClose} />
  
    <Shell init={0} 
      views={views}
      dialogs={{items: [dialogs.group, dialogs.user, dialogs.project], onClick: handleDialogOpen}}
      tabs={{items: session.tabs, active: session.history.open, onClick: changeTab }} />
  </React.Fragment>);
}

export default App;
