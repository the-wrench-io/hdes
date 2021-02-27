import React from 'react';

import { Resources } from '../Resources';



interface AssetsViewProps {
};

const AssetsView: React.FC<AssetsViewProps> = () => {
  const { session } = React.useContext(Resources.Context);
  const workspace = session.workspace;
  if(!workspace) {
    throw new Error("Can't dispaly asset because workspace is not defined!");
  }
  
  const open = session.history.open;
  const tabData = session.tabs[open];
  const blob = Object.values(workspace.snapshot.blobs).filter(b => b.id === tabData.id)[0];
  
  return (<div>{blob.src}</div>);
}

export default AssetsView;
