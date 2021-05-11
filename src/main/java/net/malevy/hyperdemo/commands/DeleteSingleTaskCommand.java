package net.malevy.hyperdemo.commands;

import lombok.Data;

@Data
public class DeleteSingleTaskCommand implements Command<String> {

    private Integer id;

}
