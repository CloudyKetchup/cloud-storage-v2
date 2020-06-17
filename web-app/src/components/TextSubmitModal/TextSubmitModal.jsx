import React, {useContext} from 'react';

import './text-submit-modal.css';

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { ReactComponent as CloseSvg } from "../../assets/icons/close.svg";


const TextSubmitModal = props => {
    const onButtonClick = () => {

        let input = document.getElementById("modal-input")
        let name = input.value
        if (name && name !== "") {
            onSubmit(name)
        }
    };

    const { 
        title = "Text Modal", 
        buttonText = "Submit", 
        placeholder = "Text here", 
        onSubmit
    } = props;

    const { theme } = useContext(ThemeContext);

    const darkStyle = {
        background: "black",
        color: Theme.LIGHT,
        fill: Theme.LIGHT
    };
    const lightStyle = {
        background: Theme.LIGHT,
        color: Theme.DARK,
        fill: Theme.DARK
    };

    const style = theme === Theme.LIGHT ? lightStyle : darkStyle

    return (
        <div className='text-submit-modal' style={style}>
            <div className='text-submit-modal-row'>
                <div>
                    <h3>{title}</h3>
                </div> 
                <div>
                    <CloseSvg className='modal-close' />
                </div>
            </div>
            <div className='text-submit-modal-row'>
                <div>
                    <input id='modal-input'  className={`modal-input ${theme === Theme.LIGHT && "modal-input-light"}`} type="text" placeholder={placeholder} />
                </div> 
                <div>
                    <button className={`modal-submit ${theme === Theme.DARK && "modal-submit-light"}`} onClick={onButtonClick}>{buttonText}</button>
                </div>
            </div>
        </div>
    );
};

export default TextSubmitModal