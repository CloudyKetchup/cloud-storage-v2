import React, { FC, useEffect, useContext } from "react";

import ContextMenu, { ContextMenuActions } from "../DirectoryItemContextMenu/ContextMenu";

import { API_URL } 							from "../../api/env.config";
import FolderClient 						from "../../api/FolderClient";
import { Folder } 							from "../../models/Directory";
import { ThemeContext, Theme } 	from "../../context/ThemeContext";
import { ContextMenuContext }		from "../../context/ContextMenuContext";
import { DirectoryContext } 		from "../../context/DirectoryContext";
import { FoldersContext }				from "../../context/FoldersContext";
import { ClipboardContext, ClipbaordItemAction} from "../../context/ClipboardContext";

import { ReactComponent as FolderSvg } 	from "../../assets/icons/folder.svg";

import "./folder-item.css";

type IProps = { data: Folder };

const FolderItem: FC<IProps> = props =>
{
	const { menuId, setMenuId }	= useContext(ContextMenuContext);
	const { setFolder }					= useContext(DirectoryContext);
	const { setItem } 					= useContext(ClipboardContext);
	const { theme } 						= useContext(ThemeContext);
	const { deleteFolder } 			= useContext(FoldersContext);
	const { id, name, path } 		= props.data;
	const folderClient 					= FolderClient.instance();

	useEffect(() =>
	{
		const div = document.getElementById(`folder-item-${id}`);

		if (div)
		{
			const onContextMenu = (e: Event) =>
			{
				e.preventDefault();

				toggleMenu();
			};

			div.addEventListener("contextmenu", onContextMenu);

			return () =>
			{
				div.removeEventListener("contextmenu", onContextMenu);
			};
		}
	});

	const substringName = (name: string): string =>
	{
		return name.length > 13 ? `${name.substring(0, 12)}...` : name;
	};

	const onDownload = () =>
	{
		const link = document.createElement("a");
		
		link.href = `${API_URL}/folder/download?path=${path.replace(/[/]/g, '%2F')}`;

		link.download = name;

		document.body.appendChild(link);
		
		link.click();

		document.body.removeChild(link);
	};

	const onCopy = () =>
	{
		setItem({ body : props.data, action : ClipbaordItemAction.COPY });
	};

	const onCut = () =>
	{
		setItem({ body : props.data, action : ClipbaordItemAction.MOVE });
	};

	const onTrash = () =>
	{
		// TODO: implement
	};

	const onDelete = async () =>
	{
		const { status } = await folderClient.remove(id);

		status === 200 && deleteFolder(id);
	};

	const actions: ContextMenuActions = {
		onDownload 	: onDownload,
		onCut 			: onCut,
		onCopy			: onCopy,
		onTrash 		: onTrash,
		onDelete		: onDelete
	};

	const toggleMenu = () =>
	{
		if (menuId === id)
		{
			setMenuId("");
		} else
		{
			setMenuId(id);
		}
	};

	return (
		<div
			id={`folder-item-${id}`}
			className={`folder-item ${ theme === Theme.DARK && "folder-item-dark" }`}
			onDoubleClick={() => setFolder(props.data)}
		>
			{
				menuId !== id
				&&
				<>
					<div className="folder-item-icon">
						<div>
							<FolderSvg />
						</div>
					</div>
					<div className="folder-item-text">
						<span>{substringName(name)}</span>
					</div>
				</>
			}
			{
				menuId === id
				&&
				<ContextMenu actions={actions} className="folder-item-contextmenu"/>
			}
		</div>
	);
};

export default FolderItem;
