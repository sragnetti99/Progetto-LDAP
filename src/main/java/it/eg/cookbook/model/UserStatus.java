package it.eg.cookbook.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class UserStatus {

    @ApiModelProperty(notes = "Common Name", position = 1, required = true, example = "sragnetti")
    private String cn;

    @ApiModelProperty(notes = "Status", position = 2, example = "Utente Esistente")
    private String status;
}
