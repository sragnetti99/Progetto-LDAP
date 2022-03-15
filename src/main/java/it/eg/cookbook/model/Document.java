package it.eg.cookbook.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel
public class Document {

    @ApiModelProperty(notes = "Id", position = 1, required = true, example = "doc-1")
    private String id;

    @ApiModelProperty(notes = "Name", position = 2, required = true, example = "Titolo")
    private String name;

    @ApiModelProperty(notes = "Description", position = 3, required = true, example = "Descrizione")
    private String description;

}
