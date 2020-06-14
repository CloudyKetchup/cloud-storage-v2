import React, { FC, CSSProperties, useContext } from "react";

import { CircularProgress } from "@material-ui/core";

import "./loading-component.css";
import { ThemeContext } from "../../context/ThemeContext";

type IProps = {
  error     : boolean,
  loading   : boolean,
  fallback  : JSX.Element,
  onLoading?: JSX.Element,
  style?    : CSSProperties
};

const Loading: FC<IProps> = ({ error, loading, fallback, onLoading, style, children }) =>
{
  const DefaultSpinner = () =>
  {
    const { theme } = useContext(ThemeContext);

    return (
      <div className="loading-default-spinner" style={style} color={theme}>
        <CircularProgress color="inherit" />
      </div>
    );
  };

  return (
    <>
      {
        loading
          ?
          onLoading || <DefaultSpinner />
          :
          error
            ?
            fallback
            :
            children
      }
    </>
  );
}

export default Loading;