import React, { useContext } from "react";

import { ThemeContext, Theme }  from "../../context/ThemeContext";
import { AppContext }           from "../../context/AppContext";

import UploadButton     from "../UploadButton/UploadButton";
import TreeView         from "../TreeView/TreeView";
import StorageStatsView from "./StroageStatsView";
import { CompactSeparator } from "../CompactSeparator/CompactSeparator";

import "./left-panel.css";
import "../../css/theme.css";

const MainLeftPanel = () =>
{
  const { theme }       = useContext(ThemeContext)
  const { rootFolder }  = useContext(AppContext);

  return (
    <div className={`main-left-panel ${ theme === Theme.DARK && "main-left-panel-dark" }`}>
      <div>
        <UploadButton/>
      </div>
      {
        rootFolder
        &&
        <>
          <CompactSeparator/>
          <TreeView folderId={rootFolder.id} style={{
            height: "calc(100% - 165px)",
            width : "calc(100% - 21px)",
            overflowX : "auto"
          }} />
        </>
      }
      <StorageStatsView theme={theme}/>
    </div>
  );
};

export default MainLeftPanel;
