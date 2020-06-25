import React, { FC, useContext, useEffect } from "react";

import ContextMenu, { ContextMenuActions } from "../DirectoryItemContextMenu/ContextMenu";

import { API_URL } 							from "../../api/env.config";
import FileClient 							from "../../api/FileClient";
import { File } 								from "../../models/Directory";
import { ThemeContext, Theme } 	from "../../context/ThemeContext";
import { ContextMenuContext }		from "../../context/ContextMenuContext";
import { FilesContext } 				from "../../context/FilesContext";
import { ClipboardContext, ClipbaordItemAction } 		from "../../context/ClipboardContext";

import { ReactComponent as FileSvg } 			from "../../assets/icons/file.svg";
import { ReactComponent as CalendarSvg } 	from "../../assets/icons/calendar.svg";

import { formatSize } from "../../utils/directory.format.utils";


import "./file-item.css";

type IProps = { data : File };

const FileItem: FC<IProps> = ({ data }) =>
{
	const { theme }							= useContext(ThemeContext);
	const { menuId, setMenuId } = useContext(ContextMenuContext);
	const { setItem } 					= useContext(ClipboardContext);
	const { deleteFile } 				= useContext(FilesContext);
	const { id, name, path, size, dateCreated, extension } = data;
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

	const formatName = (name: string): string =>
	{
		return name.length > 12 ? `${name.substring(0, 11)}...` : name;
	};

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
			<div>
				<div className="file-item-icon">
					<div>
						<FileSvg/>
					</div>
				</div>
				<div className="file-item-info">
					<div>{formatName(name)}</div>
					<div>{formatSize(size)}</div>
				</div>
			</div>
			{
				menuId === id
				?
				<ContextMenu actions={actions}/>
				:
				<div className="file-item-footer">
					<div className="file-item-date">
						<div>
							<CalendarSvg/>
						</div>
						<div>
							<h5>{dateCreated}</h5>
						</div>
					</div>
					<div>{extension}</div>
				</div>
			}
		</div>
	);
};

export default FileItem;
