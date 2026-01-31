package io.github.lv.ecs.pawn.system.work

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx

import com.badlogic.gdx.utils.TimeUtils
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.ecs.EngineContainer
import io.github.lv.ecs.pawn.component.PathComponent
import io.github.lv.ecs.pawn.component.PawnInformationComponent
import io.github.lv.ecs.pawn.component.WorkComponent
import io.github.lv.ecs.pawn.system.MyIteratingSystem
import io.github.lv.ecs.thing.component.ThingInformationComponent
import io.github.lv.pawn.WorkType
import io.github.lv.pawn.WorkType.*
import io.github.lv.ecs.pawn.state.OrderState
import io.github.lv.ui.menus.MenuManager.Companion.uiXmlPath
import io.github.lv.ui.menus.MenuManager.Companion.xmlReader

/*
* 分4步：根据优先级队列找到当前工作类型，再根据类型找到工作对象，然后根据对象找到路径，最后根据类型工作。
* */
@Component
class WorkSystem : MyIteratingSystem(
    Family.all(
        WorkComponent::class.java,
        PawnInformationComponent::class.java,
        PathComponent::class.java
    ).get()
) {
    @Inject
    lateinit var engineContainer: EngineContainer

    val period = 1f

    // 保存上次更新时间
    var lastTime: Long = 0

    override fun processEntity(entity: Entity, deltaTime: Float) {
        // TODO("Not yet implemented")
        val workComponent = entity.getComponent(WorkComponent::class.java)
        //首先，读取工作优先级的xml
        val fileName = "workType"
        val xmlFileName = fileName.removeSuffix(".xml") + ".xml"
        val file = Gdx.files.internal("$uiXmlPath/$xmlFileName")
        val rootElement = xmlReader.parse(file)
        // 根据排序后的任务执行工作
        /*
        * 根据优先级找工作,找到后
        * */
        val entities = engineContainer.pawnEngine.getEntitiesFor(
            Family.all(
                ThingInformationComponent::class.java
            ).get()
        )
        if (workComponent.workTarget == null) {
            //寻找下一个工作对象
            val find = findWork(entity, entities)
            if (!find) {
                workComponent.workType = null
            }
        } else {
            val arrive = setPosition(entity)
            /*
      * 如果已经有工作了,但是没走到，就去工作地点。
      * */
            if (true) {

            } else {
                /*
                * 如果到了，开始工作
                * */
            }
        }

    }


    override fun update(deltaTime: Float) {
        val currentTime = TimeUtils.nanoTime()
        if (lastTime == 0L) {
            lastTime = currentTime // 第一次调用时，初始化 lastTime
        }
        val elapsedTime = (currentTime - lastTime) / 1000000000.0f  // 将纳秒转为秒
        if (elapsedTime >= period) {
            lastTime = currentTime  // 更新 lastTime
            super.update(deltaTime)
        }
    }

    fun findWork(pawnEntity: Entity, entities: ImmutableArray<Entity>): Boolean {

        //TODO 优化：显然不能每个优先级都遍历一遍，应该缓存
        val workComponent = pawnEntity.getComponent(WorkComponent::class.java)
        for (workPriority in workComponent.sortedWorkPriorities) {
            for (entity in entities) {
                val thingInformationComponent = entity.getComponent(ThingInformationComponent::class.java)

            }
        }
        return false
    }

    fun findTarget(pawnEntity: Entity): Boolean {
        val workComponent = pawnEntity.getComponent(WorkComponent::class.java)

        outer@ for (workPriority in workComponent.sortedWorkPriorities) {
            inner@ for (entity in entities) {
                val thingInformationComponent = entity.getComponent(ThingInformationComponent::class.java)
                if (thingInformationComponent.isPending && equalWork(
                        thingInformationComponent.workType,
                        workPriority.workType
                    )
                ) {
                    //TODO（根据距离判断）
                    workComponent.workTarget = entity
                    workComponent.workType = workPriority.workType
                    return true
                }
            }
        }
        return false
    }

    fun setPosition(pawnEntity: Entity): Boolean {

        return false
    }

}

/**
 * @param orderState 命令类型
 * @param workType 优先级类型
 * @return 是否匹配
 */
fun equalWork(orderState: OrderState, workType: WorkType): Boolean {
    when (workType) {
        Firefight -> TODO()
        Patient -> TODO()
        Doctor -> TODO()
        BedRest -> TODO()
        Childcare -> TODO()
        Basic -> TODO()
        Warden -> TODO()
        Handle -> TODO()
        Cook -> TODO()
        Hunt -> TODO()
        Construct -> TODO()
        Grow -> TODO()
        Mine -> TODO()
        PlantCut -> TODO()
        Smith -> TODO()
        Tailor -> TODO()
        Art -> TODO()
        Craft -> TODO()
        Haul -> TODO()
        Clean -> TODO()

        Research -> TODO()
        Sleep -> TODO()
        DarkStudy -> TODO()
    }

    return false
}
