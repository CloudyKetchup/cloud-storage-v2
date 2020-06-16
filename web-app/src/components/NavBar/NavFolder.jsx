import React, { useContext } from "react";

import { DirectoryContext } from "../../context/DirectoryContext";

import { ReactComponent as Arrow } 		from "../../assets/icons/arrow-right.svg"
import { ReactComponent as HomeSvg } 	from "../../assets/icons/home.svg";

import './nav-bar.css';

const NavFolder = ({ folder, last }) =>
{
	const { setFolder } = useContext(DirectoryContext);
	const { name, root } = folder;

	return (
		<div className='nav-folder' onClick={() => setFolder(folder)}>
			<div>
				<span style={{ cursor : "pointer" }}>
					{ !root ? name : <HomeSvg style={{ marginTop : "5px" }}/> }
				</span>
			</div>
			<div>
				{ !last && <Arrow className='nav-folder-arrow' /> }
			</div>
		</div>
	)
};


export default NavFolder