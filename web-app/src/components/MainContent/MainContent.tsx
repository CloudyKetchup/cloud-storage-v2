import React, { useContext } from "react";

import DirectoryInfo    from "../DirectoryInfoHeader/DirectoryInfoHeader";
import DirectoryContent from "../DirectoryContent/DirectoryContent";
import FetchErrorBanner from "../FetchErrorBanner/FetchErrorBanner";
import Loading          from "../LoadingComponent/LoadingComponent";

import { AppContext }       from "../../context/AppContext";
import { DirectoryContext } from "../../context/DirectoryContext";

import "./main-content.css";

const MainContent = () =>
{
  const { folder } = useContext(DirectoryContext);
  const { loading, errorLoadingApp } = useContext(AppContext);

  return (
    <div className="main-content">
      <DirectoryInfo folder={folder} />
      <Loading
        loading={loading}
        error={errorLoadingApp}
        fallback={<FetchErrorBanner text="Error happened" height="calc(100% - 20px)"/>}
        style={{ height: "calc(100% - 20px)" }}
      >
        {folder && <DirectoryContent folder={folder}/>}
      </Loading>
    </div>
  );
};

export default MainContent;