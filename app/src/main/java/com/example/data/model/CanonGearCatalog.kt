package com.example.data.model

data class CanonGearPreset(
    val name: String,
    val modelCode: String,
    val category: GearCategory,
    val subcategory: String,
    val suggestedPriceHuf: Double,
    val description: String
)

object CanonGearCatalog {
    val items: List<CanonGearPreset> = listOf(
        // ==================== CAMERA BODIES ====================
        CanonGearPreset(
            name = "Canon EOS R5 Mark II",
            modelCode = "EOS R5 Mark II",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Full-Frame Mirrorless",
            suggestedPriceHuf = 1899000.0,
            description = "45MP Stacked BSI sensor, 8K 60p RAW, Eye Control AF, Accelerated Capture engine with deep learning AF"
        ),
        CanonGearPreset(
            name = "Canon EOS R6 Mark II",
            modelCode = "EOS R6 Mark II",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Full-Frame Mirrorless",
            suggestedPriceHuf = 1049000.0,
            description = "24.2MP Full-Frame sensor, 40 fps electronic burst, 6K oversampled 4K 60p, dual Pixel CMOS AF II"
        ),
        CanonGearPreset(
            name = "Canon EOS R1",
            modelCode = "EOS R1",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Pro Flagship Mirrorless",
            suggestedPriceHuf = 3199000.0,
            description = "Flagship 24.2MP Cross-type Dual Pixel Intelligent AF, 40 fps raw pre-burst, 6K 60p RAW"
        ),
        CanonGearPreset(
            name = "Canon EOS R3",
            modelCode = "EOS R3",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Pro Sports Mirrorless",
            suggestedPriceHuf = 2499000.0,
            description = "24.1MP Stacked sensor, Eye Control AF, 30 fps blackout-free burst, 6K 60p RAW"
        ),
        CanonGearPreset(
            name = "Canon EOS R5",
            modelCode = "EOS R5",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Full-Frame Mirrorless",
            suggestedPriceHuf = 1399000.0,
            description = "45MP Full-Frame, 8-stop IBIS, 8K 30p RAW, 4K 120p, Dual card slots (CFexpress + SD)"
        ),
        CanonGearPreset(
            name = "Canon EOS R6",
            modelCode = "EOS R6",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Full-Frame Mirrorless",
            suggestedPriceHuf = 749000.0,
            description = "20.1MP Full-Frame, high ISO sensitivity, 8-stop IBIS, 4K 60p, dual UHS-II SD slots"
        ),
        CanonGearPreset(
            name = "Canon EOS R8",
            modelCode = "EOS R8",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Full-Frame Mirrorless",
            suggestedPriceHuf = 589000.0,
            description = "24.2MP compact lightweight full-frame, 40 fps, uncropped 4K 60p oversampled from 6K"
        ),
        CanonGearPreset(
            name = "Canon EOS R7",
            modelCode = "EOS R7",
            category = GearCategory.CAMERA_BODY,
            subcategory = "APS-C Mirrorless",
            suggestedPriceHuf = 649000.0,
            description = "32.5MP APS-C, 30 fps electronic burst, in-body IS, Dual Pixel CMOS AF II, dual SD slots"
        ),
        CanonGearPreset(
            name = "Canon EOS R10",
            modelCode = "EOS R10",
            category = GearCategory.CAMERA_BODY,
            subcategory = "APS-C Mirrorless",
            suggestedPriceHuf = 419000.0,
            description = "24.2MP APS-C, 23 fps burst, 4K 60p cropped, compact creator body"
        ),
        CanonGearPreset(
            name = "Canon EOS R50",
            modelCode = "EOS R50",
            category = GearCategory.CAMERA_BODY,
            subcategory = "APS-C Mirrorless",
            suggestedPriceHuf = 329000.0,
            description = "24.2MP compact travel & vlog camera, 4K 30p uncropped, intuitive touch interface"
        ),
        CanonGearPreset(
            name = "Canon EOS R100",
            modelCode = "EOS R100",
            category = GearCategory.CAMERA_BODY,
            subcategory = "APS-C Mirrorless",
            suggestedPriceHuf = 219000.0,
            description = "24.1MP entry mirrorless, optical viewfinder replacement, compact RF mount entry"
        ),
        CanonGearPreset(
            name = "Canon EOS 5D Mark IV",
            modelCode = "EOS 5D Mark IV",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Full-Frame DSLR",
            suggestedPriceHuf = 899000.0,
            description = "30.4MP legendary workhorse DSLR, Dual Pixel RAW, 61-point AF system, rugged weather sealing"
        ),
        CanonGearPreset(
            name = "Canon EOS 1D X Mark III",
            modelCode = "EOS 1D X Mark III",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Pro DSLR",
            suggestedPriceHuf = 2799000.0,
            description = "20.1MP sports & press flagship DSLR, 191 AF points, 5.5K 60p 12-bit RAW video, dual CFexpress"
        ),
        CanonGearPreset(
            name = "Canon EOS 6D Mark II",
            modelCode = "EOS 6D Mark II",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Full-Frame DSLR",
            suggestedPriceHuf = 499000.0,
            description = "26.2MP Full-Frame DSLR, 45 cross-type AF points, vari-angle touchscreen"
        ),
        CanonGearPreset(
            name = "Canon EOS C70",
            modelCode = "EOS C70",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Cinema EOS",
            suggestedPriceHuf = 2199000.0,
            description = "Super 35 DGO sensor, 4K 120p, Canon Log 2/3, 16+ stops dynamic range, RF mount, built-in NDs"
        ),
        CanonGearPreset(
            name = "Canon EOS R5 C",
            modelCode = "EOS R5 C",
            category = GearCategory.CAMERA_BODY,
            subcategory = "Cinema EOS",
            suggestedPriceHuf = 1799000.0,
            description = "Dual boot cinema & photo system, 8K 60p RAW unlimited recording with active internal cooling"
        ),

        // ==================== RF LENSES ====================
        CanonGearPreset(
            name = "Canon RF 24-70mm f/2.8L IS USM",
            modelCode = "RF 24-70mm f/2.8L IS USM",
            category = GearCategory.LENS,
            subcategory = "RF Zoom Lens",
            suggestedPriceHuf = 1029000.0,
            description = "Pro standard zoom with 5-stop Image Stabilization, Nano USM autofocus, Air Sphere coating"
        ),
        CanonGearPreset(
            name = "Canon RF 70-200mm f/2.8L IS USM",
            modelCode = "RF 70-200mm f/2.8L IS USM",
            category = GearCategory.LENS,
            subcategory = "RF Zoom Lens",
            suggestedPriceHuf = 1199000.0,
            description = "Ultra-compact telephoto zoom with dual Nano USM motors and 5-stop optical IS"
        ),
        CanonGearPreset(
            name = "Canon RF 15-35mm f/2.8L IS USM",
            modelCode = "RF 15-35mm f/2.8L IS USM",
            category = GearCategory.LENS,
            subcategory = "RF Zoom Lens",
            suggestedPriceHuf = 999000.0,
            description = "Ultra-wide fast zoom for landscape and architecture with 5-stop IS and Subwavelength structure coating"
        ),
        CanonGearPreset(
            name = "Canon RF 28-70mm f/2L USM",
            modelCode = "RF 28-70mm f/2L USM",
            category = GearCategory.LENS,
            subcategory = "RF Zoom Lens",
            suggestedPriceHuf = 1399000.0,
            description = "Groundbreaking f/2 maximum aperture throughout entire zoom range, prime-lens sharpness"
        ),
        CanonGearPreset(
            name = "Canon RF 50mm f/1.2L USM",
            modelCode = "RF 50mm f/1.2L USM",
            category = GearCategory.LENS,
            subcategory = "RF Prime Lens",
            suggestedPriceHuf = 979000.0,
            description = "Legendary optical performance, dreamlike bokeh, ring-type USM, UD and aspherical elements"
        ),
        CanonGearPreset(
            name = "Canon RF 85mm f/1.2L USM",
            modelCode = "RF 85mm f/1.2L USM",
            category = GearCategory.LENS,
            subcategory = "RF Prime Lens",
            suggestedPriceHuf = 1249000.0,
            description = "Benchmark portrait lens with Blue Spectrum Refractive (BR) optics and f/1.2 speed"
        ),
        CanonGearPreset(
            name = "Canon RF 85mm f/1.2L USM DS",
            modelCode = "RF 85mm f/1.2L USM DS",
            category = GearCategory.LENS,
            subcategory = "RF Prime Lens",
            suggestedPriceHuf = 1389000.0,
            description = "Defocus Smoothing (DS) coating for the creamiest out-of-focus highlights and portraits"
        ),
        CanonGearPreset(
            name = "Canon RF 135mm f/1.8L IS USM",
            modelCode = "RF 135mm f/1.8L IS USM",
            category = GearCategory.LENS,
            subcategory = "RF Prime Lens",
            suggestedPriceHuf = 1099000.0,
            description = "Telephoto portrait prime with 5.5-stop optical IS, customizable function buttons, and ultra-smooth bokeh"
        ),
        CanonGearPreset(
            name = "Canon RF 100mm f/2.8L Macro IS USM",
            modelCode = "RF 100mm f/2.8L Macro IS USM",
            category = GearCategory.LENS,
            subcategory = "RF Macro Lens",
            suggestedPriceHuf = 619000.0,
            description = "1.4x magnification macro with Spherical Aberration (SA) control ring for variable bokeh softness"
        ),
        CanonGearPreset(
            name = "Canon RF 100-500mm f/4.5-7.1L IS USM",
            modelCode = "RF 100-500mm f/4.5-7.1L IS USM",
            category = GearCategory.LENS,
            subcategory = "RF Zoom Lens",
            suggestedPriceHuf = 1299000.0,
            description = "Versatile wildlife and sports super-telephoto zoom with 5-stop optical IS and dual Nano USM"
        ),
        CanonGearPreset(
            name = "Canon RF 24-105mm f/4L IS USM",
            modelCode = "RF 24-105mm f/4L IS USM",
            category = GearCategory.LENS,
            subcategory = "RF Zoom Lens",
            suggestedPriceHuf = 569000.0,
            description = "Essential all-in-one travel zoom with constant f/4, Nano USM, and 5-stop IS"
        ),
        CanonGearPreset(
            name = "Canon RF 24-105mm f/2.8L IS USM Z",
            modelCode = "RF 24-105mm f/2.8L IS USM Z",
            category = GearCategory.LENS,
            subcategory = "RF Hybrid Zoom Lens",
            suggestedPriceHuf = 1499000.0,
            description = "Hybrid cinema/still standard zoom with constant f/2.8, iris ring, optional power zoom adapter"
        ),
        CanonGearPreset(
            name = "Canon RF 35mm f/1.8 IS Macro STM",
            modelCode = "RF 35mm f/1.8 IS Macro STM",
            category = GearCategory.LENS,
            subcategory = "RF Prime Lens",
            suggestedPriceHuf = 229000.0,
            description = "Compact 35mm street and environmental prime with 0.5x macro and 5-stop hybrid IS"
        ),
        CanonGearPreset(
            name = "Canon RF 50mm f/1.8 STM",
            modelCode = "RF 50mm f/1.8 STM",
            category = GearCategory.LENS,
            subcategory = "RF Prime Lens",
            suggestedPriceHuf = 89000.0,
            description = "The classic 'nifty fifty' in ultra-compact RF format with PMo aspherical element"
        ),
        CanonGearPreset(
            name = "Canon RF 16mm f/2.8 STM",
            modelCode = "RF 16mm f/2.8 STM",
            category = GearCategory.LENS,
            subcategory = "RF Prime Lens",
            suggestedPriceHuf = 139000.0,
            description = "Ultra-compact 16mm ultra-wide pancake lens, ideal for vlogging, astrophotography, interiors"
        ),
        CanonGearPreset(
            name = "Canon RF 28mm f/2.8 STM",
            modelCode = "RF 28mm f/2.8 STM",
            category = GearCategory.LENS,
            subcategory = "RF Prime Lens",
            suggestedPriceHuf = 149000.0,
            description = "Ultra-slim pancake prime with 3 high-precision plastic molded aspherical elements"
        ),
        CanonGearPreset(
            name = "Canon RF 85mm f/2 Macro IS STM",
            modelCode = "RF 85mm f/2 Macro IS STM",
            category = GearCategory.LENS,
            subcategory = "RF Prime Lens",
            suggestedPriceHuf = 289000.0,
            description = "Compact portrait and half-size macro prime with 5-stop optical IS"
        ),
        CanonGearPreset(
            name = "Canon RF 100-400mm f/5.6-8 IS USM",
            modelCode = "RF 100-400mm f/5.6-8 IS USM",
            category = GearCategory.LENS,
            subcategory = "RF Zoom Lens",
            suggestedPriceHuf = 299000.0,
            description = "Compact, lightweight entry telephoto zoom with Nano USM and 5.5-stop IS"
        ),
        CanonGearPreset(
            name = "Canon RF 200-800mm f/6.3-9 IS USM",
            modelCode = "RF 200-800mm f/6.3-9 IS USM",
            category = GearCategory.LENS,
            subcategory = "RF Zoom Lens",
            suggestedPriceHuf = 949000.0,
            description = "Long-range super-telephoto zoom for wildlife, birds, and aviation with 5.5-stop IS"
        ),

        // ==================== EF LENSES ====================
        CanonGearPreset(
            name = "Canon EF 24-70mm f/2.8L II USM",
            modelCode = "EF 24-70mm f/2.8L II USM",
            category = GearCategory.LENS,
            subcategory = "EF Zoom Lens",
            suggestedPriceHuf = 799000.0,
            description = "Industry standard professional DSLR zoom with Super UD elements and ring USM"
        ),
        CanonGearPreset(
            name = "Canon EF 70-200mm f/2.8L IS III USM",
            modelCode = "EF 70-200mm f/2.8L IS III USM",
            category = GearCategory.LENS,
            subcategory = "EF Zoom Lens",
            suggestedPriceHuf = 949000.0,
            description = "Pro telephoto zoom with Air Sphere Coating (ASC), 3.5-stop IS, fluorite elements"
        ),
        CanonGearPreset(
            name = "Canon EF 16-35mm f/2.8L III USM",
            modelCode = "EF 16-35mm f/2.8L III USM",
            category = GearCategory.LENS,
            subcategory = "EF Zoom Lens",
            suggestedPriceHuf = 849000.0,
            description = "Pro wide-angle zoom with edge-to-edge sharpness, dual large-diameter glass-molded elements"
        ),
        CanonGearPreset(
            name = "Canon EF 50mm f/1.4 USM",
            modelCode = "EF 50mm f/1.4 USM",
            category = GearCategory.LENS,
            subcategory = "EF Prime Lens",
            suggestedPriceHuf = 189000.0,
            description = "Classic fast standard prime with micro USM and Gaussian optical design"
        ),
        CanonGearPreset(
            name = "Canon EF 85mm f/1.4L IS USM",
            modelCode = "EF 85mm f/1.4L IS USM",
            category = GearCategory.LENS,
            subcategory = "EF Prime Lens",
            suggestedPriceHuf = 649000.0,
            description = "Fast aperture portrait prime with 4-stop optical Image Stabilization and weather sealing"
        ),
        CanonGearPreset(
            name = "Canon EF 100mm f/2.8L Macro IS USM",
            modelCode = "EF 100mm f/2.8L Macro IS USM",
            category = GearCategory.LENS,
            subcategory = "EF Macro Lens",
            suggestedPriceHuf = 419000.0,
            description = "1:1 magnification macro with Hybrid Image Stabilization compensating angular and shift movements"
        ),

        // ==================== SPEEDLITES & LIGHTING ====================
        CanonGearPreset(
            name = "Canon Speedlite EL-1",
            modelCode = "Speedlite EL-1",
            category = GearCategory.LIGHTING,
            subcategory = "Flash & Speedlite",
            suggestedPriceHuf = 479000.0,
            description = "Flagship professional flash with active fan cooling system, Li-ion pack, down to 1/8192 power"
        ),
        CanonGearPreset(
            name = "Canon Speedlite EL-5",
            modelCode = "Speedlite EL-5",
            category = GearCategory.LIGHTING,
            subcategory = "Flash & Speedlite",
            suggestedPriceHuf = 199000.0,
            description = "Multi-Function Shoe flash powered by LP-EL Li-ion battery, Guide Number 60, built-in modeling light"
        ),
        CanonGearPreset(
            name = "Canon Speedlite 600EX II-RT",
            modelCode = "Speedlite 600EX II-RT",
            category = GearCategory.LIGHTING,
            subcategory = "Flash & Speedlite",
            suggestedPriceHuf = 239000.0,
            description = "Pro on-camera radio wireless E-TTL flash, Guide Number 60, continuous burst heat resistance"
        ),
        CanonGearPreset(
            name = "Canon Speedlite 430EX III-RT",
            modelCode = "Speedlite 430EX III-RT",
            category = GearCategory.LIGHTING,
            subcategory = "Flash & Speedlite",
            suggestedPriceHuf = 129000.0,
            description = "Compact versatile flash with radio transmission, bounce adapter, Guide Number 43"
        ),
        CanonGearPreset(
            name = "Canon Macro Ring Lite MR-14EX II",
            modelCode = "MR-14EX II",
            category = GearCategory.LIGHTING,
            subcategory = "Macro Lighting",
            suggestedPriceHuf = 269000.0,
            description = "Twin circular flash tubes with independent ratio firing for close-up and medical photography"
        ),
        CanonGearPreset(
            name = "Canon Speedlite Transmitter ST-E3-RT",
            modelCode = "ST-E3-RT V2",
            category = GearCategory.LIGHTING,
            subcategory = "Wireless Trigger",
            suggestedPriceHuf = 139000.0,
            description = "2-way radio transmitter controlling up to 15 flashes across 5 groups with second-curtain sync"
        ),

        // ==================== ACCESSORIES & ADAPTERS ====================
        CanonGearPreset(
            name = "Canon Control Ring Mount Adapter EF-EOS R",
            modelCode = "Control Ring EF-EOS R",
            category = GearCategory.ACCESSORY,
            subcategory = "Lens Adapter",
            suggestedPriceHuf = 89000.0,
            description = "Adapts EF/EF-S lenses to RF bodies with an assignable tactile control ring for ISO/shutter/aperture"
        ),
        CanonGearPreset(
            name = "Canon Drop-In Filter Mount Adapter EF-EOS R (C-PL)",
            modelCode = "Drop-In Filter Adapter C-PL",
            category = GearCategory.ACCESSORY,
            subcategory = "Lens Adapter",
            suggestedPriceHuf = 159000.0,
            description = "EF-to-RF adapter with drop-in circular polarizing filter, allows filters on bulbous front elements"
        ),
        CanonGearPreset(
            name = "Canon Drop-In Filter Mount Adapter EF-EOS R (V-ND)",
            modelCode = "Drop-In Filter Adapter V-ND",
            category = GearCategory.ACCESSORY,
            subcategory = "Lens Adapter",
            suggestedPriceHuf = 179000.0,
            description = "EF-to-RF adapter with drop-in variable neutral density filter (ND 1.5 - ND 9)"
        ),
        CanonGearPreset(
            name = "Canon BG-R10 Battery Grip",
            modelCode = "BG-R10",
            category = GearCategory.ACCESSORY,
            subcategory = "Battery Grip",
            suggestedPriceHuf = 149000.0,
            description = "Vertical battery grip for EOS R5, R5 II, R6, R6 II; accommodates two LP-E6NH/LP-E6P batteries"
        ),
        CanonGearPreset(
            name = "Canon BG-R20 Battery Grip",
            modelCode = "BG-R20",
            category = GearCategory.ACCESSORY,
            subcategory = "Battery Grip",
            suggestedPriceHuf = 189000.0,
            description = "Enhanced ergonomic vertical grip designed for high-power demands of EOS R5 Mark II and R6 Mark II"
        ),
        CanonGearPreset(
            name = "Canon LP-E6P High-Draw Battery",
            modelCode = "LP-E6P",
            category = GearCategory.ACCESSORY,
            subcategory = "Battery & Power",
            suggestedPriceHuf = 42000.0,
            description = "6A continuous discharge high-output Li-ion battery enabling full 8K 60p and high-speed AF features"
        ),
        CanonGearPreset(
            name = "Canon LP-E6NH Rechargeable Battery",
            modelCode = "LP-E6NH",
            category = GearCategory.ACCESSORY,
            subcategory = "Battery & Power",
            suggestedPriceHuf = 36000.0,
            description = "2130mAh standard Li-ion battery pack supporting in-camera USB charging"
        ),
        CanonGearPreset(
            name = "Canon Extender RF 1.4x",
            modelCode = "Extender RF 1.4x",
            category = GearCategory.ACCESSORY,
            subcategory = "Teleconverter",
            suggestedPriceHuf = 249000.0,
            description = "Increases telephoto reach by 1.4x with minimal optical degradation and full communication"
        ),
        CanonGearPreset(
            name = "Canon Extender RF 2.0x",
            modelCode = "Extender RF 2.0x",
            category = GearCategory.ACCESSORY,
            subcategory = "Teleconverter",
            suggestedPriceHuf = 299000.0,
            description = "Doubles telephoto focal length while maintaining weather resistance and optical stabilization"
        )
    )

    fun getByCategory(category: GearCategory): List<CanonGearPreset> {
        return items.filter { it.category == category }
    }
}
