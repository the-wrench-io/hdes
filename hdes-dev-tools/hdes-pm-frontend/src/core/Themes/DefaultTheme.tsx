import { createMuiTheme } from '@material-ui/core/styles';

const theme = createMuiTheme({
  palette: {
    type: 'dark',
    primary: {
      main: '#41ead4',
    },
    secondary: {
      main: '#fbff12',
    },
    error: {
      main: '#ff206e',
    },
    background: {
      default: '#0c0f0a',
    },
  },

  props: {
    MuiPaper: {
      square: true,
    },
  },
  
  overrides: {
    MuiButton: {
      root: {
        borderRadius: 0,
      },
    },
    
    MuiDialogTitle: {
      root: {
        borderTop: '1px solid #fbff12',
        borderRight: '1px solid #fbff12',
        borderLeft: '1px solid #fbff12'
      }
    },
    MuiDialogActions: {
      root: {
        borderLeft: '1px solid #fbff12',
        borderRight: '1px solid #fbff12',
        borderBottom: '1px solid #fbff12'
      }
    },
    
    MuiCssBaseline: {
      '@global': {
        '*::-webkit-scrollbar': {
          width: '0.4em'
        },
        '*::-webkit-scrollbar-track': {
          '-webkit-box-shadow': 'inset 0 0 6px rgba(0,0,0,0.00)'
        },
        '*::-webkit-scrollbar-thumb': {
          backgroundColor: '#41ead4',
          outline: '1px solid slategrey'
        }
      }
    }
  }
});

export { theme };
