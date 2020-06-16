import React, { useContext, useEffect, useState } from 'react';

import NavFolder from './NavFolder';

import FolderClient 						from '../../api/FolderClient';
import { ThemeContext, Theme } 	from "../../context/ThemeContext";
import { DirectoryContext } 		from '../../context/DirectoryContext';

import { ReactComponent as BurgerSvg } 	from "../../assets/icons/three-bars-menu.svg";
import { ReactComponent as MoonSvg } 		from "../../assets/icons/moon.svg";
import { ReactComponent as SunSvg } 		from "../../assets/icons/sun.svg";

import { isLastById } from '../../utils/array.utils';

import './nav-bar.css';

const NavBar = () =>
{
	const { folder } = useContext(DirectoryContext);
	const { theme, setTheme } = useContext(ThemeContext);
	const [folders, setFolders] = useState([]);

	const folderClient = FolderClient.instance();

	useEffect(() =>
	{
		const fetchFolders = async () =>
		{
			if (folder)
			{
				const { data } = await folderClient.getPreviousFolders(folder.id);

				data && setFolders(data);
			}
		};

		fetchFolders();
	}, [folder]);

	const darkStyle = {
		background: Theme.DARK,
		color			: Theme.LIGHT,
		fill			: Theme.LIGHT
	};

	const lightStyle = {
		background: Theme.LIGHT,
		color			: Theme.DARK,
		fill			: Theme.DARK
	};

	const style = theme === Theme.LIGHT ? lightStyle : darkStyle;

	return (
		<div className="nav-bar" style={style}>
			<div>
				{folders.map(folder =>
					<NavFolder
						key={folder.id}
						folder={folder}
						last={isLastById(folders, folder)}
					/>
				)}
			</div>
			<div className="nav-bar-control">
				<div>
					{
						theme === Theme.LIGHT
							?
							<MoonSvg onClick={() => setTheme(Theme.DARK)} />
							:
							<SunSvg onClick={() => setTheme(Theme.LIGHT)} />
					}
				</div>
				<div>
					<BurgerSvg />
				</div>
			</div>
		</div>
	);
};

export default NavBar;