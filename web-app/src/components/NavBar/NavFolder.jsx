import React from "react";

import { ReactComponent as Arrow } from "../../assets/icons/arrow-right.svg"

import './nav-bar.css';

// TODO: NavItem -> NavFolder

const NavFolder = ({ folder, last }) => (
    <div className='nav-folder' >
        <div>
            <span>{folder.name}</span>
        </div> 
        
        <div>
            {!last && <Arrow className='nav-folder-arrow' />}
        </div>
    </div>
);


export default NavFolder