
import groovy.xml.MarkupBuilder

def map = [
    key1:'value1',
    key2:'value2',
    key3:'value3',
    key4:'value4',
    key5 : [
        key1:'value1',
        key2:'value2',
        key3:'value3',
        key4:'value4',
        key5 : [
            key1:'value1',
            key2:'value2',
            key3:'value3',
            key4:'value4'
        ]
    ]
]

Closure renderMap( Map map ){
   return {  
     for ( entry in map ){ 
        switch( entry.value.getClass() ){
            case Map :
                "${entry.key}" renderMap( entry.value )
                break 
            default :
                "${entry.key}"( "${entry.value}" )
                break
        }
      }
    }
}

def writer = new StringWriter()
new MarkupBuilder(writer).root renderMap(map) 

println writer.toString()