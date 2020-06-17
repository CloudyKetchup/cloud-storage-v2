import React, { FC, useContext, CSSProperties } from "react";

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { ReactComponent as CopySvg } 		from "../../assets/icons/copy.svg";
import { ReactComponent as CutSvg } 		from "../../assets/icons/scissors.svg";
import { ReactComponent as TrashSvg } 	from "../../assets/icons/trash.svg";
import { ReactComponent as DeleteSvg } 	from "../../assets/icons/delete.svg";

import "./context-menu.css";

export type ContextMenuActions = {
	onCut 		: (id: string) => void
	onCopy		: (id: string) => void
	onDelete	: (id: string) => void
};

type IProps = {
	folderId: string,
	actions : ContextMenuActions
};

const ContextMenu: FC<IProps> = ({ folderId, actions }) =>
{
	const { theme } = useContext(ThemeContext);
	const { onCopy, onCut, onDelete } = actions;

	const sectionDark = theme === Theme.DARK && "folder-contextmenu-section-dark";

	return (
		<div
			id={`folder-${folderId}-contextmenu`}
			className={`folder-contextmenu ${ theme === Theme.DARK ? "folder-contextmenu-dark" : "" }`}
		>
			<div
				onClick={() => onCopy(folderId)}
				className={`folder-contextmenu-section ${sectionDark}`}
			>
				<div>
					<CopySvg/>
				</div>
				<div>
					<h5>Copy</h5>
				</div>
			</div>
			<div
				className={`folder-contextmenu-section ${sectionDark}`}
				onClick={() => onCut(folderId)}
			>
				<div>
					<CutSvg/>
				</div>
				<div>
					<h5>Cut</h5>
				</div>
			</div>
			<div
				className={`folder-contextmenu-section ${sectionDark}`}
				onClick={() => onDelete(folderId)}
			>
				<div>
					<DeleteSvg/>
				</div>
				<div>
					<h5>Delete</h5>
				</div>
			</div>
 		</div>
	);
};

export default ContextMenu;