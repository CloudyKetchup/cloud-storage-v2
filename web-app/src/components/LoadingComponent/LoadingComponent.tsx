import React, { FC, CSSProperties } from "react";

import { CircularProgress } from "@material-ui/core";

import "./loading-component.css";

type IProps = {
  error     : boolean,
  loading   : boolean,
  fallback  : JSX.Element,
  onLoading?: JSX.Element,
  style?    : CSSProperties
};

const Loading: FC<IProps> = ({ error, loading, fallback, onLoading, style, children }) =>
{
  const DefaultSpinner = () => (
    <div className="loading-default-spinner" style={style}>
      <CircularProgress color="inherit"/>
    </div>
  );

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