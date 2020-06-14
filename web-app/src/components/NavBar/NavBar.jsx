import React, { useContext } from 'react';

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { ReactComponent as Burger } from "../../assets/icons/three-bars-menu.svg";

import NavFolder from "./NavFolder";

import './nav-bar.css';

const NavBar = () => {

    const {theme} = useContext(ThemeContext);

    const darkStyle = {
        background: Theme.DARK,
        color: Theme.LIGHT,
        fill: Theme.LIGHT
    };
    const lightStyle = {
        background: Theme.LIGHT,
        color: Theme.DARK,
        fill: Theme.DARK
    };

    const style = theme == Theme.LIGHT ? lightStyle : darkStyle

    const isLast = folder => {
        const last = folders[folders.length - 1];
        return folder.id === last.id
    };

    return(
        <div className='nav-bar' style={style} >
            { folders.map( folder => <NavFolder folder={folder} last={isLast(folder)} />) }
            <div className="nav-bars-menu-btn">
                <Burger className="nav-bars-menu"/>
            </div>
        </div>
    );
};

export default NavBar