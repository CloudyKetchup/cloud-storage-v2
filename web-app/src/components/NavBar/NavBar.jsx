import React, { useContext } from 'react';

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { ReactComponent as BurgerSvg }  from "../../assets/icons/three-bars-menu.svg";
import { ReactComponent as MoonSvg }    from "../../assets/icons/moon.svg";
import { ReactComponent as SunSvg }     from "../../assets/icons/sun.svg";

import './nav-bar.css';

const NavBar = () =>
{
    const { theme, setTheme } = useContext(ThemeContext);

    const darkStyle = {
        background  : Theme.DARK,
        color       : Theme.LIGHT,
        fill        : Theme.LIGHT
    };
    const lightStyle = {
        background  : Theme.LIGHT,
        color       : Theme.DARK,
        fill        : Theme.DARK
    };

    const style = theme === Theme.LIGHT ? lightStyle : darkStyle;

    return (
        <div className="nav-bar" style={style}>
            <div>
                {/* NavFolders here */}
            </div>
            <div className="nav-bar-control">
                <div>
                    {
                        theme === Theme.LIGHT
                        ?
                        <MoonSvg onClick={() => setTheme(Theme.DARK)}/>
                        :
                        <SunSvg onClick={() => setTheme(Theme.LIGHT)}/>
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