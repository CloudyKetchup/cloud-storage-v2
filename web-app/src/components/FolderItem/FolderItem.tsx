import React, { FC, useState, useContext } from "react";

import { Folder } from "../../models/Directory";

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { ReactComponent as FolderSvg } from "../../assets/icons/folder.svg";

import "./folder-item.css";

type IProps = { data: Folder };

const FolderItem: FC<IProps> = ({ data }) =>
{
	const { theme } = useContext(ThemeContext);
	const { id, name } = data;

	const substringName = (name: string) : string =>
	{
		return name.length > 14 ? `${name.substring(0, 13)}...` : name;
	};

	return (
		<div className={`folder-item ${ theme === Theme.DARK && "folder-item-dark" }`}>
			<div className="folder-item-icon">
				<div>
					<FolderSvg />
				</div>
			</div>
			<div className={`${ theme === Theme.DARK && "folder-text-dark" }`}>
				<span>{substringName(name)}</span>
			</div>
		</div>
	);
};

export default FolderItem;