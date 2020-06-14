import React, { FC, CSSProperties, useContext } from "react";

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { ReactComponent as GhostSvg } from "../../assets/icons/ghost.svg";

import "./fetch-error-banner.css";

type IProps = {
  text?   : string
  height? : string,
  width?  : string
};

const FetchErrorBanner: FC<IProps> = ({ text = "Error fetching data", height, width }) =>
{
  const { theme } = useContext(ThemeContext);

  const darkStyle: CSSProperties = {
    color   : Theme.LIGHT,
    fill    : Theme.LIGHT,
    height  : height,
    width   : width
  };

  const lightStyle: CSSProperties = {
    color   : Theme.DARK,
    fill    : Theme.DARK,
    height  : height,
    width   : width
  };

  const style = theme == Theme.LIGHT ? lightStyle : darkStyle

  return (
    <div className="fetch-error-banner" style={style}>
      <div>
        <div>
          <GhostSvg/>
        </div>
        <div>
          <h3>{text}</h3>
        </div>
      </div>
    </div>
  );
};

export default FetchErrorBanner;