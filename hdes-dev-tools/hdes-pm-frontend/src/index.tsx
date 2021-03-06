import React from 'react';
import ReactDOM from 'react-dom';
import { ThemeProvider } from '@material-ui/core/styles';
import { Resources, Backend } from './core/Resources';
import { theme } from './core/Themes';
import App from './App';
import reportWebVitals from './reportWebVitals';

declare global {
  interface Window { hdesconfig: Backend.ServerConfig | undefined }
}


  
  /* testing 
  window.hdesconfig = {
    ctx:      "http://localhost:8080/hdes-pm/rest-api/resources",
    users:    "http://localhost:8080/hdes-pm/rest-api/resources/users",
    groups:   "http://localhost:8080/hdes-pm/rest-api/resources/groups",
    projects: "http://localhost:8080/hdes-pm/rest-api/resources/projects",
    headers: {}
  };
  /* */

ReactDOM.render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <Resources.Provider>
        <App />
      </Resources.Provider>
    </ThemeProvider>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
