import React, {useContext} from 'react';

import { ThemeContext, Theme } from "../../context/ThemeContext";

import { ReactComponent as CloseSvg } from "../../assets/icons/close.svg";

import './text-submit-modal.css';

const TextSubmitModal = props =>
{
  const submit = () =>
  {
    let input = document.getElementById("modal-input")
    let name = input.value

    if (name && name !== "")
    {
      onSubmit(name)
    }
  };

  const { 
    title = "Text Modal", 
    buttonText = "Submit", 
    placeholder = "Text here", 
    onSubmit,
    onClose
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

  const style = theme === Theme.LIGHT ? lightStyle : darkStyle;

  return (
    <div className='text-submit-modal' style={style}>
      <div className='text-submit-modal-row'>
        <div>
          <h5>{title}</h5>
        </div> 
        <div onClick={onClose}>
          <CloseSvg className='modal-close' />
        </div>
      </div>
        <div className='text-submit-modal-row'>
          <div>
            <input
            id="modal-input"
            className={`modal-input ${theme === Theme.LIGHT && "modal-input-light"}`}
            placeholder={placeholder}
            />
          </div> 
        <div>
          <button
            className={`modal-submit ${theme === Theme.DARK && "modal-submit-light"}`}
            onClick={submit}
          >
            {buttonText}
          </button>
        </div>
      </div>
    </div>
  );
};

export default TextSubmitModal