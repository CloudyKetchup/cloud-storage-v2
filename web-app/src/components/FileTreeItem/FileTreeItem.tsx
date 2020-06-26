import React, { FC, useContext } from "react";

import { ReactComponent as FileSvg } from "../../assets/icons/file.svg";

import "./file-tree-item.css";
import { ThemeContext, Theme } from "../../context/ThemeContext";

type IProps = { name : string };

const FileTreeItem: FC<IProps> = ({ name }) =>
{
  const { theme } = useContext(ThemeContext);

  return (
    <div className={`file-tree-item ${ theme === Theme.DARK && "file-tree-item-dark" }`}>
      <div>
        <FileSvg />
      </div>
      <div>
        <span>{name.substring(0, 14)}</span>
      </div>
    </div>
  );
};

export default FileTreeItem;