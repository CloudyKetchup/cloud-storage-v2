import React, { useContext } from "react";

import FolderClient       from "../../api/FolderClient";
import FileClient         from "../../api/FileClient";
import { Folder }         from "../../models/Directory";
import { FolderMoveData, FileMoveData } from "../../models/MoveData";

import { ClipboardItem, ClipbaordItemAction, ClipboardContext } from "../../context/ClipboardContext";
import { ThemeContext, Theme }  from "../../context/ThemeContext";
import { DirectoryContext }     from "../../context/DirectoryContext";
import { FilesContext }         from "../../context/FilesContext";
import { FoldersContext }       from "../../context/FoldersContext";

import { ReactComponent as ClipboardSvg } from "../../assets/icons/clipboard.svg";

const Clipboard = ({ item } : { item : ClipboardItem }) =>
{
	const { theme } 	        = useContext(ThemeContext);
  const { folder } 	        = useContext(DirectoryContext);
  const { addFile }         = useContext(FilesContext);
  const { addFolder }       = useContext(FoldersContext);
  const { clearClipboard }  = useContext(ClipboardContext);
  const folderClient        = FolderClient.instance();
  const fileClient          = FileClient.instance();

  const isFolder = (): boolean => (item.body as Folder).root !== undefined

  const moveData = () =>
  {
    if (isFolder())
    {
      return { folder : item.body, targetFolder : folder };
    } else
    {
      return { file : item.body, folder : folder };
    }
  }

  const copyFolder = async () =>
  {
    const { data } = await folderClient.copy(moveData() as FolderMoveData)

    if (data)
    {
      addFolder(data);
      clearClipboard();
    }
  };

  const copyFile = async () =>
  {
    const { data } = await fileClient.copy(moveData() as FileMoveData);

    if (data)
    {
      addFile(data);
      clearClipboard();
    }
  };

  const moveFolder = async () =>
  {
    const { data } = await folderClient.move(moveData() as FolderMoveData)

    if (data)
    {
      addFolder(data);
      clearClipboard();
    }
  };

  const moveFile = async () =>
  {
    const { data } = await fileClient.move(moveData() as FileMoveData);

    if (data)
    {
      addFile(data);
      clearClipboard();
    }
  };

	const onClick = () =>
	{
		switch (item.action)
		{
			case ClipbaordItemAction.COPY: isFolder() ? copyFolder() : copyFile();
				break;
			case ClipbaordItemAction.MOVE: isFolder() ? moveFolder() : moveFile();
				break;
		};
	};

	return (
		<div
			title={item.body.name}
			className={`clipboard-item ${ theme === Theme.DARK && "clipboard-item-dark" }`}
			onClick={onClick}
		>
			<ClipboardSvg />
		</div>
	);
};

export default Clipboard;