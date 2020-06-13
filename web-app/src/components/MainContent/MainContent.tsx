import React, { useContext, useEffect } from "react";

import DirectoryInfo from "../DirectoryInfoHeader/DirectoryInfoHeader";

import { DirectoryContext } from "../../context/DirectoryContext";

import "./main-content.css";

const MainContent = () =>
{
  const { folder } = useContext(DirectoryContext);

  return (
    <div className="main-content">
      <DirectoryInfo folder={folder} />
    </div>
  );
};

export default MainContent;