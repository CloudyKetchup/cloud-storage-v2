import React, { useContext } from "react";

import { ThemeContext, Theme }  from "../../context/ThemeContext";

import MainContent from "../MainContent/MainContent";

import "./main.css";

const Main = () =>
{
  const { theme } = useContext(ThemeContext);

  const darkStyle = {
    background: Theme.DARK,
    color: Theme.LIGHT
  };

  const lightStyle = {
    background: Theme.LIGHT,
    color: Theme.DARK
  };

  const style = theme == Theme.LIGHT ? lightStyle : darkStyle

  return (
    <div className="main" style={style}>
      {/* NavBar here */}
      <MainContent />
    </div>
  );
};

export default Main;