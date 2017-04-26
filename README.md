# README #

### What is this repository for? ###

data2viz is a data visualization library for several platform: browser, jvm, android, ... It relies on kotlin which
 compiles on these platforms.

data2viz proposes to develop data visualizations through a completely typed DSL. It simplifies the programmation by 
helping developer with IDE’s suggestions based on the current context.

### Current status ###

data2viz is in its early age. It should not be used in production now. We are experimenting various DSL
 implementations. DSL will be validated after having implemented all the use cases which will take few months.

### Some DSL samples  

The internal DSL allow the creation of datavisualization using hierarchical 
code that should be easy to understand

```kotlin
g {
    transform {
        translate(margin.left, margin.top)
    }
    
    rect {
        width = totalWidth - margin.horizontalMargins
        height = totalHeight - margin.verticalMargins
        fill = rgba(0, 0, 0, .1)
    }
}
```

In that code, `g` adds a new group. The next `{` starts a new block of code that is
applicable in the current context. Inside a group, we can apply a transformation. The 
`transform {` code starts a new block of code to define the properties of the transformation.

Having a typed DSL, the IDE proposes accurate suggestions depending on the position of 
  the caret. Inside a `transform` block, we can call `translate`, `rotate`, `scale`, 
  `skewX`, `skewY`,... 

The `rect {` code opens a block for adding a rectangle and configure it. Its width
height, and fill color are defined using an affectation. Again, having a strong DSL
allow to benefit from the IDE assistance to choose the correct values. `fill` is a
property of type `Color`. It can be created from a call on `rgb`, `rgba`, `hsl`, `hsla` functions
 or converted from an hex string. `"#ab1212".col()` or just by referencing a CSS color 
 (`steelblue`, `grey`,... ).

In any case, due to the strong typed language used, any error will be notified during the 
compilation phase. 
