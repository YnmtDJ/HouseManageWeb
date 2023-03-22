/*
 * Copyright (c) 2021 Martin Davis
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

/**
 * Classes for triangulating polygons.
 * {@link ConstrainedDelaunayTriangulator} can be used to provide high-quality
 * near-Delaunay triangulations of polygonal geometry.
 * The {@link PolygonTriangulator} produces lower-quality but faster triangulations. 
 */
package org.locationtech.jts.triangulate.polygon;