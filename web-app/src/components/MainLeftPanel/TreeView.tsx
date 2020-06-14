import React, { FC } from "react";

import { CompactSeparator } from "../CompactSeparator/CompactSeparator";

import { PropsWithTheme } from "../../models/PropsWithTheme";

const TreeView: FC<PropsWithTheme> = ({ theme }) =>
{
  return (
    <div className="tree-view">
      <CompactSeparator/>
    </div>
  );
};

export default TreeView;