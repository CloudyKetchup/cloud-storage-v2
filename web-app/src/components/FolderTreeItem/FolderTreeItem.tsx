import React, { FC, useState, useContext } from "react";

import { Folder }               from "../../models/Directory";
import { ThemeContext, Theme }  from "../../context/ThemeContext";
import { DirectoryContext }     from "../../context/DirectoryContext";

import TreeView from "../TreeView/TreeView";

import { ReactComponent as ArrowSvg } from "../../assets/icons/arrow-fill-right.svg";

import "./folder-tree-item.css";

type IProps = { folder : Folder };

const FolderTreeItem: FC<IProps> = ({ folder }) =>
{
  const [expanded, setExpanded] = useState<boolean>(false);
  const { setFolder }           = useContext(DirectoryContext);
  const { theme }               = useContext(ThemeContext);

  return (
    <>
      <div
        className={`folder-tree-item ${ theme === Theme.DARK && "folder-tree-item-dark" }`}
        onDoubleClick={() => setFolder(folder)}
        onClick={() => setExpanded(!expanded)}
        style={{ background : expanded ? theme === Theme.DARK ? "black" : "#f3f3f3" : "" }}
      >
        <div>
          <ArrowSvg style={{ transform : expanded ? "rotate(90deg)" : "unset" }}/>
        </div>
        <div>
          <span>{folder.name}</span>
        </div>
      </div>
      {expanded && <TreeView folderId={folder.id} />}
    </>
  );
};

export default FolderTreeItem;