import React, { MouseEvent } from 'react';

import { makeStyles } from '@material-ui/core/styles';
import Popover from '@material-ui/core/Popover';
import Typography from '@material-ui/core/Typography';
import Link from '@material-ui/core/Link';
import EditOutlinedIcon from '@material-ui/icons/EditOutlined';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import IconButton from '@material-ui/core/IconButton';
import VisibilityOutlinedIcon from '@material-ui/icons/VisibilityOutlined';
import TabUnselectedOutlinedIcon from '@material-ui/icons/TabUnselectedOutlined';
import DeleteOutlineIcon from '@material-ui/icons/DeleteOutline';


import { Title, Summary, DateFormat } from '.././Views';
import { Resources, Backend } from '.././Resources';

 
const useStyles = makeStyles((theme) => ({
  seeMore: {
    marginTop: theme.spacing(3),
  },
  popover: {
    pointerEvents: 'none',
    
  },
  paper: {
    //backgroundColor: 'transparent',
    //padding: theme.spacing(1),
  },
}));


interface UserViewProps {
  top?: number,
  seeMore?: () => void,
  onDelete: (user: Backend.UserResource) => void,
  onSelect: (props: {builder: Backend.UserBuilder, edit?: boolean, activeStep?: number}) => void
};

const UsersView: React.FC<UserViewProps> = ({top, seeMore, onSelect, onDelete}) => {
  const { service, session } = React.useContext(Resources.Context);
  const { users } = session.data;

  const classes = useStyles();
  
  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);
  const [openPopoverRow, setOpenPopoverRow] = React.useState<Backend.UserResource>();
  const handlePopoverOpen = (event: any, row: Backend.UserResource) => {
    setOpenPopoverRow(row);
    setAnchorEl(event.currentTarget);
  }
  const handlePopoverClose = () => {
    setAnchorEl(null)
  };
  const openPopover = Boolean(anchorEl);
  
  return (
    <React.Fragment>
      {top ? <Title>Recent Users</Title> : null}
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell></TableCell>
            <TableCell>Email</TableCell>
            <TableCell>External Id</TableCell>
            <TableCell>Groups</TableCell>
            <TableCell>Projects</TableCell>
            <TableCell>Created</TableCell>
            <TableCell></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.filter(u => u.user.status === 'ENABLED').map((row) => (
            <TableRow key={row.user.id}>
              <TableCell>
                <Typography aria-haspopup="true"
                  aria-owns={openPopover ? 'mouse-over-popover' : undefined}
                  onMouseEnter={(event) => handlePopoverOpen(event, row)} onMouseLeave={handlePopoverClose} >
                  {row.user.name}
                </Typography>
              </TableCell>
              <TableCell>
                <Typography aria-haspopup="true"
                  aria-owns={openPopover ? 'mouse-over-popover' : undefined}
                  onMouseEnter={(event) => handlePopoverOpen(event, row)} onMouseLeave={handlePopoverClose} >
                  <VisibilityOutlinedIcon fontSize='small'/>
                </Typography>
              </TableCell>
              <TableCell>
                {row.user.email}
              </TableCell>
              <TableCell>
                {row.user.externalId}
              </TableCell>
              <TableCell>{Object.keys(row.groups).length}</TableCell>
              <TableCell>{Object.keys(row.projects).length}</TableCell>
              <TableCell><DateFormat>{row.user.created}</DateFormat></TableCell>
              <TableCell align="right">
                <IconButton size="small" onClick={() => onSelect({builder: service.users.builder(row)})} color="inherit">
                  <TabUnselectedOutlinedIcon />
                </IconButton>
                <IconButton size="small" onClick={() => onSelect({builder: service.users.builder(row), edit: true})} color="inherit">
                  <EditOutlinedIcon/>
                </IconButton>
                <IconButton size="small" onClick={() => onDelete(row)} color="inherit">
                  <DeleteOutlineIcon/>
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      
      <Popover id="mouse-over-popover"
          className={classes.popover}
          classes={{paper: classes.paper }}
          open={openPopover}
          anchorEl={anchorEl}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
          transformOrigin={{ vertical: 'top', horizontal: 'left' }}
          onClose={handlePopoverClose}
          disableRestoreFocus>
        {openPopoverRow ? <Summary resource={openPopoverRow}/> : null}
      </Popover>
      
      { seeMore ? 
        (<div className={classes.seeMore}>
          <Link color="primary" href="#" onClick={(event: MouseEvent) => {
            event.preventDefault();
            seeMore();
          }}>
            See more users
          </Link>
        </div>) :
        null
      }
    </React.Fragment>
  );
}

export default UsersView;

