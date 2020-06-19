import React, { FC, useContext } from "react";

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { ReactComponent as DownlaodSvg } 	from "../../assets/icons/download.svg";
import { ReactComponent as CopySvg } 			from "../../assets/icons/copy.svg";
import { ReactComponent as CutSvg } 			from "../../assets/icons/scissors.svg";
import { ReactComponent as TrashSvg } 		from "../../assets/icons/trash.svg";
import { ReactComponent as DeleteSvg } 		from "../../assets/icons/delete.svg";

import "./context-menu.css";

export type ContextMenuActions = {
	onDownload : () => void
	onCopy 		 : () => void
	onCut 		 : () => void
	onTrash		 : () => void
	onDelete 	 : () => void
};

type IProps = { actions : ContextMenuActions };

const ContextMenu: FC<IProps> = ({ actions }) =>
{
	const { theme } = useContext(ThemeContext);
	const { onDownload, onCopy, onCut, onTrash, onDelete } = actions;

	return (
		<div className={`file-contextmenu ${ theme === Theme.DARK ? "file-contextmenu-dark" : "" }`}>
			<div onClick={onDownload}>
				<DownlaodSvg />
			</div>
			<div onClick={onCopy}>
				<CopySvg />
			</div>
			<div onClick={onCut}>
				<CutSvg />
			</div>
			<div onClick={onTrash}>
				<TrashSvg />
			</div>
			<div onClick={onDelete}>
				<DeleteSvg />
			</div>
		</div>
	);
};

export default ContextMenu;