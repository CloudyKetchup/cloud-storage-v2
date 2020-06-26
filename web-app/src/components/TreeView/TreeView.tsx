import React, { useEffect, useState, FC, CSSProperties } from "react";

import FolderClient         from "../../api/FolderClient";
import { DirectoryContent } from "../../models/DirectoryContent";

import FolderTreeItem   from "../FolderTreeItem/FolderTreeItem";
import FileTreeItem     from "../FileTreeItem/FileTreeItem";

import "./tree-view.css";

type IProps = {
  folderId : string
  style?   : CSSProperties
};

const TreeView: FC<IProps> = ({ folderId, style }) =>
{
  const [items, setItems]     = useState<DirectoryContent | undefined>();
  const [error, setError]     = useState<boolean>(false);
  const folderClient          = FolderClient.instance();

  useEffect(() =>
  {
    const fetch = async () =>
    {
      const { data, error } = await folderClient.getContent(folderId);

      if (data)
      {
        setItems(data)
      } else if (error)
      {
        setError(true);
      }
    };

    fetch();
  }, []);

  return (
    <div className="tree-view" style={style}>
      {items?.folders.map(folder => <FolderTreeItem key={folder.id} folder={folder} />)}
      {items?.files.map(file => <FileTreeItem key={file.id} name={file.name} />)}
    </div>
  );
};

export default TreeView;