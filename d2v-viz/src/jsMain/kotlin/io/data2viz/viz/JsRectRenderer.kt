/*
 * Copyright (c) 2018-2019. data2viz sàrl.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.data2viz.viz

import org.w3c.dom.*

fun RectNode.render(context: CanvasRenderingContext2D) {

	fill?.let {
		context.fillStyle = it.toCanvasPaint(context)
		context.fillRect(x, y, width, height)
	}

	stroke?.let {
		context.strokeStyle = it.toCanvasPaint(context)
		context.strokeRect(x, y, width, height)
	}

}