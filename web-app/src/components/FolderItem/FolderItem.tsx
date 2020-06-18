import React, { FC, useEffect, useState, useContext } from "react";

import ContextMenu, { ContextMenuActions } from "./ContextMenu";

import { Folder } 							from "../../models/Directory";
import { ThemeContext, Theme } 	from "../../context/ThemeContext";
import { ContextMenuContext }		from "../../context/ContextMenuContext";

import { ReactComponent as FolderSvg } 	from "../../assets/icons/folder.svg";
import { ReactComponent as DotsSvg } 		from "../../assets/icons/vertical-dots.svg";
import { ReactComponent as CloseSvg }		from "../../assets/icons/delete.svg";

import "./folder-item.css";

//TODO: directory items context for only one options menu per child

type IProps = { data: Folder };

const FolderItem: FC<IProps> = ({ data }) =>
{
	// const [menu, setMenu] = useState<boolean>(false);
	const { menuId, setMenuId } = useContext(ContextMenuContext);
	const { theme } 						= useContext(ThemeContext);
	const { id, name } 					= data;

	useEffect(() =>
	{
		const optionsHover = async () =>
		{
			const div 		= document.getElementById(`folder-item-${id}`);
			const options = document.getElementById(`folder-item-${id}-options`);

			if (div && options)
			{
				const onOptionsHover = async () =>
				{
					options.style.display = "unset";
				};

				const outOptionsHover = async () =>
				{
					options.style.display = "none";
				};

				div.addEventListener("mouseover", onOptionsHover);
				div.addEventListener("mouseout", outOptionsHover);

				return () =>
				{
					div.removeEventListener("mouseover", onOptionsHover);
					div.removeEventListener("mouseout", outOptionsHover);
				};
			}
		}

		optionsHover();
	}, []);

	const substringName = (name: string): string =>
	{
		return name.length > 13 ? `${name.substring(0, 12)}...` : name;
	};

	const onCopy = async (id: string) =>
	{
		// TODO: implement
	};

	const onCut = async (id: string) =>
	{
		// TODO: implement
	};

	const onDelete = async (id: string) =>
	{
		// TODO: implement
	};

	const actions: ContextMenuActions = {
		onCut 	: onCut,
		onCopy	: onCopy,
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
			<div className="folder-item-icon">
				<div>
					<FolderSvg />
				</div>
			</div>
			<div className="folder-item-text">
				<span>{substringName(name)}</span>
			</div>
			<div
				id={`folder-item-${id}-options`}
				className="folder-item-options"
				style={{ display : "none" }}
				onClick={toggleMenu}
			>
				{
					menuId === id
					?
					<CloseSvg fill={ theme === Theme.LIGHT ? "#181818" : "white" }/>
					:
					<DotsSvg fill={ theme === Theme.LIGHT ? "#181818" : "white" }/>
				}
			</div>
			{
				menuId === id
				&&
				<ContextMenu folderId={id} actions={actions}/>
			}
		</div>
	);
};

export default FolderItem;