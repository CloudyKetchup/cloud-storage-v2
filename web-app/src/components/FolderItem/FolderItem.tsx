import React, { FC, useEffect, useState, useContext } from "react";

import ContextMenu, { ContextMenuActions } from "../DirectoryItemContextMenu/ContextMenu";

import { Folder } 							from "../../models/Directory";
import { ThemeContext, Theme } 	from "../../context/ThemeContext";
import { ContextMenuContext }		from "../../context/ContextMenuContext";

import { ReactComponent as FolderSvg } 	from "../../assets/icons/folder.svg";
import { ReactComponent as DotsSvg } 		from "../../assets/icons/vertical-dots.svg";
import { ReactComponent as CloseSvg }		from "../../assets/icons/delete.svg";

import "./folder-item.css";

type IProps = { data: Folder };

const FolderItem: FC<IProps> = ({ data }) =>
{
	const { menuId, setMenuId } = useContext(ContextMenuContext);
	const { theme } 						= useContext(ThemeContext);
	const { id, name } 					= data;

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
		// TODO: implement
	};

	const onCopy = () =>
	{
		// TODO: implement
	};

	const onCut = () =>
	{
		// TODO: implement
	};

	const onTrash = () =>
	{
		// TODO: implement
	};

	const onDelete = () =>
	{
		// TODO: implement
	};

	const actions: ContextMenuActions = {
		onDownload : onDownload,
		onCut 	: onCut,
		onCopy	: onCopy,
		onTrash : onTrash,
		onDelete: onDelete
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