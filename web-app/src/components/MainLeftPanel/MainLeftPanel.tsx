import React, { useContext, FC } from "react";

import { ThemeContext, Theme }  from "../../context/ThemeContext";

import UploadButton     from "../UploadButton/UploadButton";
import TreeView         from "./TreeView";
import StorageStatsView from "./StroageStatsView";

import "./left-panel.css";
import "../../css/theme.css";

const MainLeftPanel = () =>
{
  const { theme } = useContext(ThemeContext)

  const darkTheme = {
    background: "rgb(35, 35, 35)",
    color: "white"
  };

  const lightTheme = {
    background: "#dedede",
    color: "#181818"
  };

  return (
    <div
      style={ theme == Theme.DARK ? darkTheme : lightTheme }
      className="main-left-panel"
    >
      <div>
        <UploadButton/>
      </div>
      <TreeView theme={theme}/>
      <StorageStatsView theme={theme}/>
    </div>
  );
};

export default MainLeftPanel;
