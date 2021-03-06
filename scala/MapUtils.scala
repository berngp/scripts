
import scala.collection.mutable.{ LinkedHashMap => LinkedMutableMap }
import scala.collection.immutable.{ ListMap }


object MapUtils {

     def order[K,V](list:List[K], map:Map[K,V]):Map[K,V] = {
        val _map = LinkedMutableMap[K,V]()
        list.foreach{ key:K => 
            if ( !_map.contains(key) ) 
                _map += ( key->map(key) ) 
        }
        ListMap() ++ _map

    }

    implicit def wrapAsMapUtils[K,V]( map:Map[K,V]) = new MapUtilsWrapper(map)
}


class MapUtilsWrapper[K,V](map:Map[K,V]){
  def orderBy(list:List[K]):Map[K,V] = MapUtils.order(list,map)
}

import MapUtils._

val map = Map(2 -> 'B', 3 -> 'C', 5 -> 'E', 1 -> 'A', 4 -> 'D')

val list= List(5,4,3,2,1)

val ordered = map.orderBy(list)

val expected =  Map(5->'E',4->'D',3->'C',2->'B',1->'A') 

assert( ordered  == expected )

println(ordered) 






