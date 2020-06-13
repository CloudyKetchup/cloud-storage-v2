import React, { useContext, FC } from "react";

import { Folder } from "../../models/Directory";

import { FoldersContext }       from "../../context/FoldersContext";
import { FilesContext }         from "../../context/FilesContext";
import { ThemeContext, Theme }  from "../../context/ThemeContext";

import { ReactComponent as InfoSvg } from "../../assets/icons/info.svg";

import "./directory-info-header.css";

type IProps = {
  folder?: Folder
};

const DirectoryInfo: FC<IProps> = ({ folder }) =>
{
  const { theme }   = useContext(ThemeContext);
  const { folders } = useContext(FoldersContext);
  const { files }   = useContext(FilesContext);

  const itemsCountLight = {
    color: "rgb(69, 69, 69)"
  }

  const itemsCountDark = {
    color: "white"
  }

  const itemsCountTheme = theme == Theme.DARK ? itemsCountDark : itemsCountLight

  return (
    <div className={`directory-info-header ${theme == Theme.DARK ? "dark" : "light" }`}>
      <div className="directory-title">
        <h1>{folder?.name}</h1>
      </div>
      <div className="directory-items-count" style={itemsCountTheme}>
        {folders.length + files.length} items
      </div>
      <div className="directory-info-button">
        <button>
          <InfoSvg/>
        </button>
      </div>
    </div>
  );
};

export default DirectoryInfo;