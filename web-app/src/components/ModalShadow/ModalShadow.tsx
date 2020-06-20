import React, { FC, useContext } from "react";

import { ThemeContext, Theme } from "../../context/ThemeContext";

import "./modal-shadow.css";

const ModalShadow: FC = ({ children }) =>
{
	const { theme } = useContext(ThemeContext);

	return (
		<div className={`modal-shadow ${ theme === Theme.DARK ? "modal-shadow-dark" : "" }`}>
			<div>
				{children}
			</div>
		</div>
	);
};

export default ModalShadow;