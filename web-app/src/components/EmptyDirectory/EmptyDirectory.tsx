import React, { FC, useContext } from "react";

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { ReactComponent as FolderSvg } from "../../assets/icons/folder.svg";

import "./empty-directory.css";

type IProps = { text?: string };

const EmptyDirectory: FC<IProps> = ({ text = "Directory empty" }) =>
{
	const { theme } = useContext(ThemeContext);

	return (
		<div className="empty-directory">
			<div className="empty-directory-content">
				<div>
					<FolderSvg/>
				</div>
				<div>
					<h3 style={{ color : theme === Theme.DARK ? "white" : "black" }}>
						{text}
					</h3>
				</div>
			</div>
		</div>
	);
}

export default EmptyDirectory;