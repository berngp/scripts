import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder


String xmlString = 
"""
<tree>
    <branch>
        <branch>
            <leaf>brown</leaf>
            <leaf>brown</leaf>
            <otherleaf>green</otherleaf>
            <otherleaf>green</otherleaf>
            <bird>twit</bird>
        </branch>
        <leaf>brown</leaf>
        <leaf>brown</leaf>
        <otherleaf>green</otherleaf>
        <otherleaf>green</otherleaf>
        <bird>twit</bird>
    </branch>
    <branch>
            <leaf>brown</leaf>
            <leaf>brown</leaf>
            <otherleaf>green</otherleaf>
            <otherleaf>green</otherleaf>
            <bird>twit</bird>
    </branch>
    <leaf>brown</leaf>
    <leaf>brown</leaf>
    <otherleaf>green</otherleaf>
    <otherleaf>green</otherleaf>
    <bird>twit</bird>
    <swing>
        <chords>2</chords>
        <color>red</color>
        <material>al</material>
    </swing>
</tree>
"""
    
def root =  new XmlSlurper().parseText(xmlString)
def map = [:] as LinkedHashMap

doWithLeaf = { leaf ->
}

doWithBranch = { branch ->
}

root.breadthFirst().each { n ->
    println "${n.name()}:${n.children().size()}"
    /*
    if ( n.children().size() ) {
        doWithBranch( n )
        def key = n.name()
        map[key] = map[key] ?: [ ] as LinkedHashSet
        
        def p = n.parent()
        while (p?.parent() != null) {
            key = "${p.name()}.${key}"
            p = p.parent()
        }
        map[key] = "${n.value()[0]}"
    }*/
}

    