import React, { FC, useContext, useEffect } from "react";

import ContextMenu, { ContextMenuActions } from "../DirectoryItemContextMenu/ContextMenu";

import { File } 								from "../../models/Directory";
import { ThemeContext, Theme } 	from "../../context/ThemeContext";
import { ContextMenuContext }		from "../../context/ContextMenuContext";

import { ReactComponent as FileSvg } 			from "../../assets/icons/file.svg";
import { ReactComponent as CalendarSvg } 	from "../../assets/icons/calendar.svg";

import { formatSize } from "../../utils/directory.format.utils";

import "./file-item.css";

type IProps = { data : File };

const FileItem: FC<IProps> = ({ data }) =>
{
	const { theme }							= useContext(ThemeContext);
	const { menuId, setMenuId } = useContext(ContextMenuContext);
	const { id, name, size, dateCreated, extension } = data;

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
		// TODO: implement
	};

	const onCopy = async () =>
	{
		// TODO: implement
	};

	const onCut = async () =>
	{
		// TODO: implement
	};

	const onTrash = async () =>
	{
		// TODO: implement
	};

	const onDelete = async () =>
	{
		// TODO: implement
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