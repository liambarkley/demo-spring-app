package com.buildcache.demo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WidgetController {

    private final WidgetRepository widgets;

    public WidgetController(WidgetRepository widgets) {
        this.widgets = widgets;
    }

    @GetMapping("/widgets")
    public List<Widget> list() {
        return widgets.findAll();
    }

    @PostMapping("/widgets")
    public Widget create(@RequestBody CreateWidgetRequest request) {
        return widgets.save(new Widget(request.name()));
    }

    record CreateWidgetRequest(String name) {
    }
}
