package com.github.ericytsang.lib.iteratortolistadapter

import java.util.ArrayList
import java.util.LinkedHashSet

/**
 * Created by surpl on 5/8/2016.
 */
// todo: add test cases!
class IteratorToListAdapter<T>(private val iterator:Iterator<T>):List<T>
{
    override fun get(index:Int):T = generator.generateIfPossibleUntil({index in it.indices})[index]
    override fun indexOf(element:T):Int = generator.generateIfPossibleUntil({it.contains(element)}).indexOf(element)
    override fun lastIndexOf(element:T):Int = generator.generateIfPossibleUntil({false}).lastIndexOf(element)
    override fun subList(fromIndex:Int,toIndex:Int):List<T> = generator.generateIfPossibleUntil({fromIndex in it.indices && toIndex in it.indices}).subList(fromIndex,toIndex)
    override val size:Int get() = generator.generateIfPossibleUntil({false}).size
    override fun contains(element:T):Boolean = generator.generateIfPossibleUntil({it.contains(element)}).contains(element)
    override fun containsAll(elements:Collection<T>):Boolean = generator.generateIfPossibleUntil({it.containsAll(elements)}).containsAll(elements)
    override fun isEmpty():Boolean = !iterator().hasNext()
    override fun equals(other:Any?):Boolean = generator.generateIfPossibleUntil({false}).equals(other)
    override fun hashCode():Int = generator.generateIfPossibleUntil({false}).hashCode()
    override fun toString():String = generator.generateIfPossibleUntil({false}).toString()
    override fun iterator():Iterator<T> = listIterator()
    override fun listIterator():ListIterator<T> = listIterator(0)
    override fun listIterator(index:Int):ListIterator<T> = object:ListIterator<T>
    {
        private var nextElementIndex = index
        override fun hasNext():Boolean = generator.generateIfPossibleUntil({nextIndex() in it.indices}).let {nextIndex() in it.indices}
        override fun hasPrevious():Boolean = generator.generateIfPossibleUntil({previousIndex() in it.indices}).let {previousIndex() in it.indices}
        override fun next():T = generator.getElementAt(nextElementIndex++)
        override fun previous():T = generator.getElementAt(--nextElementIndex)
        override fun nextIndex():Int = nextElementIndex
        override fun previousIndex():Int = nextElementIndex-1
    }

    private val generator = object
    {
        private val generatedElementsList = ArrayList<T>()

        fun getElementAt(index:Int):T
        {
            generateIfPossibleUntil({index in generatedElementsList.indices})
            return generatedElementsList[index]
        }

        fun generateIfPossibleUntil(predicate:(List<T>)->Boolean):List<T>
        {
            synchronized(this)
            {
                while (canGenerateNext() && !predicate(generatedElementsList))
                {
                    generateNext()
                }
            }
            return generatedElementsList
        }

        private fun canGenerateNext():Boolean
        {
            return iterator.hasNext()
        }

        private fun generateNext():Unit
        {
            generatedElementsList.add(iterator.next())
        }
    }
}