import React, { FC, useContext, useEffect } from "react";

import { ContextMenuActions } from "../DirectoryItemContextMenu/ContextMenu";
import BasicView							from "./BasicView";
import ImageView							from "./ImageView";

import { API_URL } 							from "../../api/env.config";
import FileClient 							from "../../api/FileClient";
import { File } 								from "../../models/Directory";
import { ThemeContext, Theme } 	from "../../context/ThemeContext";
import { ContextMenuContext }		from "../../context/ContextMenuContext";
import { FilesContext } 				from "../../context/FilesContext";
import { ClipboardContext, ClipbaordItemAction } 		from "../../context/ClipboardContext";

import "./file-item.css";

type IProps = {
	data 	 : File
	image? : boolean
};

const FileItem: FC<IProps> = ({ data, data: { id, name, path, extension }, image }) =>
{
	const { theme }							= useContext(ThemeContext);
	const { menuId, setMenuId } = useContext(ContextMenuContext);
	const { setItem } 					= useContext(ClipboardContext);
	const { deleteFile } 				= useContext(FilesContext);
	const fileClient						= FileClient.instance();

	useEffect(() =>
	{
		const div = document.getElementById(`file-item-${id}`);

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

	const onDownload = async () =>
	{
		const link = document.createElement("a");
		
		link.href = `${API_URL}/file/download?path=${path.replace(/[/]/g, '%2F')}`;

		link.download = name;

		document.body.appendChild(link);
		
		link.click();

		document.body.removeChild(link);
	};

	const onCopy = async () =>
	{
		setItem({ body : data, action : ClipbaordItemAction.COPY });
	};

	const onCut = async () =>
	{
		setItem({ body : data, action : ClipbaordItemAction.MOVE });
	};

	const onTrash = async () =>
	{
		// TODO: implement
	};

	const onDelete = async () =>
	{
		const { status } = await fileClient.remove(data);

		status === 200 && deleteFile(id);
	};

	const toggleMenu = () => setMenuId(menuId === id ? "" : id);

	const actions: ContextMenuActions = {
		onDownload	: onDownload,
		onCopy			: onCopy,
		onCut				: onCut,
		onTrash			: onTrash,
		onDelete		: onDelete
	};

	return (
		<div
			id={`file-item-${id}`}
			className={`file-item ${ theme === Theme.DARK ? "file-item-dark" : "" }`}
		>
			{
				image
				?
				<ImageView file={data} contextMenu={id === menuId} actions={actions} />
				:
				<BasicView file={data} contextMenu={id === menuId} actions={actions} />
			}
		</div>
	);
};

export default FileItem;
