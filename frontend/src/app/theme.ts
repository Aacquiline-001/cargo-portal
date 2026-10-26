import { definePreset } from '@primeuix/themes';
import Aura from '@primeuix/themes/aura';

// Aura theme, recoloured: teal for buttons/links, graphite for dark surfaces
export const CargoTheme = definePreset(Aura, {
  semantic: {
    primary: {
      50: '{teal.50}',
      100: '{teal.100}',
      200: '{teal.200}',
      300: '{teal.300}',
      400: '{teal.400}',
      500: '{teal.500}',
      600: '{teal.600}',
      700: '{teal.700}',
      800: '{teal.800}',
      900: '{teal.900}',
      950: '{teal.950}'
    },
    colorScheme: {
      light: {
        primary: {
          color: '{teal.600}',
          contrastColor: '#ffffff',
          hoverColor: '{teal.700}',
          activeColor: '{teal.800}'
        }
      },
      dark: {
        primary: {
          color: '{teal.400}',
          contrastColor: '#06201d',
          hoverColor: '{teal.300}',
          activeColor: '{teal.200}'
        },
        surface: {
          0: '#ffffff',
          50: '#e6f1ef',
          100: '#c9d8d5',
          200: '#a9bcb8',
          300: '#8aa5a1',
          400: '#6b8480',
          500: '#4e6461',
          600: '#364846',
          700: '#243331',
          800: '#172220',
          900: '#0f1716',
          950: '#0b0f14'
        }
      }
    }
  }
});