import React, { FC, useContext } from "react";

import { ThemeContext, Theme } from "../../context/ThemeContext";

import "./compact-separator.css"

type IProps = {
  side?: "left" | "right"
  color?: string
};

export const CompactSeparator: FC<IProps> = props =>
{
  const { theme } = useContext(ThemeContext);

  return (
    <div className="compact-separator">
      <div style={{
        float: props.side,
        background: props.color || theme == Theme.LIGHT ? "#181818" : "white"
      }}
      />
    </div>
  );
};