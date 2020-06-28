import React, { FC, useContext } from "react";

import { File } from "../../models/Directory";

import ContextMenu, { ContextMenuActions } from "../DirectoryItemContextMenu/ContextMenu";

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { API_URL } from "../../api/env.config.js";

type IProps = {
  file 				: File
  contextMenu : boolean
  actions 		: ContextMenuActions
};

const ImageView: FC<IProps> = ({ file: { path, name, extension }, contextMenu, actions }) =>
{
	const { theme } = useContext(ThemeContext);

	const themeDark = () => theme === Theme.DARK;

	return (
		<>
			<div className="file-item-image-view">
				<div className="file-item-image" style={{ opacity: contextMenu ? "0.5" : "1" }}>
					<img src={`${API_URL}/file/download?path=${path.replace(/[/]/g, '%2F')}`}/>
				</div>
				<div
					className={
						`image-view-footer
						${ themeDark() && "image-view-footer-dark" }
						${ contextMenu && (themeDark() ? "image-view-footer-expand-dark" : "image-view-footer-expand") }
						`}
				>
					{
						!contextMenu
						&&
						<>
							<div>
								<span>{name.substring(0, 10)}</span>
							</div>
							<div>
								<span>{extension}</span>
							</div>
						</>
					}
				</div>
				{
					contextMenu
					&&
					<ContextMenu
						className="image-view-contextmenu"
						styles={{ background : themeDark() ? "black" : "white" }}
						actions={actions}
					/>
				}
			</div>
		</>
	);
};

export default ImageView;