import React, { FC } from "react";

import { File } from "../../models/Directory";

import ContextMenu, { ContextMenuActions } from "../DirectoryItemContextMenu/ContextMenu";

import { ReactComponent as FileSvg } 			from "../../assets/icons/file.svg";
import { ReactComponent as CalendarSvg } 	from "../../assets/icons/calendar.svg";

import { formatSize } from "../../utils/directory.format.utils";

const formatName = (name: string): string =>
{
  return name.length > 12 ? `${name.substring(0, 11)}...` : name;
};

type IProps = {
  file        : File
  contextMenu : boolean
  actions     : ContextMenuActions
};

const BasicView: FC<IProps> = ({
  file: { name, size, dateCreated, extension },
  contextMenu,
  actions
}) => (
  <>
    <div className="file-item-body">
      <div className="file-item-icon">
        <div>
          <FileSvg />
        </div>
      </div>
      <div className="file-item-info">
        <div>{formatName(name)}</div>
        <div>{formatSize(size)}</div>
      </div>
    </div>
    {
      contextMenu
      ?
      <ContextMenu actions={actions} />
      :
      <div className="file-item-footer">
        <div className="file-item-date">
          <div>
            <CalendarSvg />
          </div>
          <div>
            <h5>{dateCreated}</h5>
          </div>
        </div>
        <div>{extension}</div>
      </div>
    }
  </>
);

export default BasicView;