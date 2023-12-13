package com.daniel.docify.model;

import com.daniel.docify.model2.FunctionModel;
import com.daniel.docify.model2.StructModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the file in any project
 * it provides useful information about the file
 */
public abstract class FileInfoModel implements Serializable{


    /**
     * @brief   This method returns all the function and struct names
     *          in a single file
     */
    public abstract List<String> getItemNames();
}
